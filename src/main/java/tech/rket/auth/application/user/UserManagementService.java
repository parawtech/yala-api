package tech.rket.auth.application.user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.validation.constraints.Email;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.application.tenant.exception.TenantDoesNotExistsException;
import tech.rket.auth.application.user.command.*;
import tech.rket.auth.application.user.config.TokenConfig;
import tech.rket.auth.application.user.exception.*;
import tech.rket.auth.application.user.info.OAuth;
import tech.rket.auth.application.user.info.UserInfo;
import tech.rket.auth.domain.core.service.AuthInvitationPredicate;
import tech.rket.auth.domain.core.service.UserPasswordEncoder;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantRepository;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserFillRepository;
import tech.rket.auth.domain.core.user.UserRepository;
import tech.rket.auth.domain.core.user.commands.*;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.core.user.entity.Session;
import tech.rket.auth.domain.core.user.entity.UserRegisterInvitation;
import tech.rket.auth.presentation.rest.api.OtpInfo;
import tech.rket.auth.presentation.rest.api.OtpResult;
import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;
import tech.rket.shared.infrastructure.phonelib.PhoneNumberHelper;

import java.util.*;

@Component
public class UserManagementService {
    private final UserRepository userRepository;
    private final UserFillRepository userFillRepository;
    private final TenantRepository tenantRepository;
    private final UserPasswordEncoder passwordEncoder;
    private final TokenConfig tokenConfig;
    private final Algorithm algorithm;
    private final AuthInvitationPredicate predicate;
    private final DefaultTenantFinder finder;
    private final OtpSender otpSender;

    public UserManagementService(UserRepository userRepository, UserFillRepository userFillRepository, TenantRepository tenantRepository, UserPasswordEncoder passwordEncoder, TokenConfig tokenConfig, AuthInvitationPredicate predicate, DefaultTenantFinder finder, OtpSender otpSender) {
        this.userRepository = userRepository;
        this.userFillRepository = userFillRepository;
        this.tenantRepository = tenantRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenConfig = tokenConfig;
        this.algorithm = Algorithm.HMAC512(tokenConfig.getSecretKey());
        this.predicate = predicate;
        this.finder = finder;
        this.otpSender = otpSender;
    }

    @Transactional
    public UserInfo create(UserRegisterCommand command) {
        if (!command.hasAuth()) {
            throw new UsernameNotFoundException("Mobile or email address is missing");
        }
        if (existsByAuth(command.mobile(), command.email())) {
            throw new UserIsAlreadyExistException(command.email());
        }
        String locale = command.locale() == null ? LocaleContextHolder.getLocale().toString() : command.locale();
        var userCreate = new UserCreate(userRepository.generateID(), locale, command.mobile(), command.email());
        User user = User.create(userCreate)
                .throwIfFailure(DomainConstraintViolationException::new)
                .value();
        user.setPassword(passwordEncoder, command.password());
        createDefaultMembership(user, command.invitedTenantId());
        userRepository.save(user);
        changeRegisterInvitationToJoinInvitation(user);
        userRepository.save(user);
        return new UserInfo(user.getId(), user.getMobile(), user.getEmail(), null);
    }

    private void changeRegisterInvitationToJoinInvitation(User user) {
        userFillRepository.fillRegisterInvitations(user);

        Optional<UserRegisterInvitation> invitation = user.getRegisterInvitations()
                .stream().filter(f -> f.tenant().getId().equals(user.getDefaultMembership().getTenant().getId()))
                .findFirst();
        invitation.ifPresent(user.getRegisterInvitations()::remove);

        List<DomainConstraintViolation> violations = user.getRegisterInvitations().stream()
                .map(e -> new UserJoinInvite(
                        e.inviter(),
                        e.tenant(),
                        e.role(),
                        e.expiredAt()
                ))
                .flatMap(e -> user.inviteJoin(predicate, e).violations().stream())
                .toList();
        if (!violations.isEmpty()) {
            throw new DomainConstraintViolationException(violations);
        }
    }

    private void createDefaultMembership(User user, Long invitedTenantId) {
        AppointedTenant appointed = finder.appoint(user.getEmail(), invitedTenantId);
        user.addDefaultMembership(new DefaultMembershipAdd(appointed.tenant(), appointed.role()));
    }

    public void updatePassword(Long id, AdminUpdatePasswordCommand newPassword) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setPassword(passwordEncoder, newPassword.newPassword());
        userRepository.save(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.disable();
        userRepository.save(user);
    }

    public void updateProfile(Long id, UserUpdateProfileCommand command) {
        User user = userRepository.findById(id).orElseThrow();
        user.updateProfile(new UserProfileUpdate(command.profile(), command.locale()))
                .throwIfFailure(DomainConstraintViolationException::new);
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void leftMembership(Long id, Long tenantIdentifier) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Membership membership = user.remove(new MembershipRemove(tenantIdentifier))
                .throwIfFailure(DomainConstraintViolationException::new)
                .value().membership();
        userRepository.delete(user, membership);
    }

    public OAuth login(UserLoginCommand userLoginCommand) {
        User user = userRepository.findByEmail(userLoginCommand.email())
                .orElseThrow(() -> new UserLoginAuthDoesNotFoundException(userLoginCommand.email()));
        Tenant tenant = userLoginCommand.tenant() == null ?
                user.getDefaultMembership().getTenant() :
                tenantRepository.findById(userLoginCommand.tenant()).orElseThrow(() -> new TenantDoesNotExistsException(userLoginCommand.tenant()));
        if (!user.matchPassword(passwordEncoder, userLoginCommand.password())) {
            throw new UserIsNotAuthenticatedException(user.getId());
        }
        Session session = user.add(
                        new SessionCreate(
                                tenant.getId(),
                                tokenConfig.getAccessTokenExpireSeconds(),
                                tokenConfig.getRefreshTokenExpireSeconds())
                )
                .throwIfFailure(DomainConstraintViolationException::new)
                .value().session();

        userRepository.save(user);

        return OAuth.create(
                access(user, session.id(), tenant),
                refresh(user, session.id(), tenant),
                session.expiredAt().toEpochMilli(),
                session.refreshableUntil().toEpochMilli());
    }

    public void logout() {
        UserLoginInfo loginInfo = UserLoginInfo.getCurrent();
        User user = userRepository.findById(loginInfo.userId()).orElseThrow(() -> new UserNotFoundException(loginInfo.userId()));
        Membership membership = user.remove(new MembershipRemove(loginInfo.tenantId()))
                .throwIfFailure(DomainConstraintViolationException::new)
                .value().membership();
        userRepository.delete(user, membership, loginInfo.sessionId());
    }


    private String access(User user, UUID sessionId, Tenant tenant) {
        Session session = user.findSession(tenant.getId(), sessionId).orElseThrow();
        String roleId = user.find(tenant.getId()).orElseThrow().getRole();
        Role role = tenant.getRole(roleId).orElseThrow();
        List<Map<String, String>> permissionsDetails = role.getPermissions().stream()
                .map(p -> {
                    Map<String, String> permissionDetails = new HashMap<>();
                    permissionDetails.put("id", p.id());
                    permissionDetails.put("name", p.name());
                    return permissionDetails;
                })
                .toList();
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("token_type", "ACCESS_TOKEN");
        return JWT.create()
                .withHeader(headers)
                .withSubject("" + user.getId())
                .withJWTId(session.id().toString())
                .withClaim("user.email", user.getEmail())
                .withClaim("user.profile", user.getName())
                .withClaim("user.locale", user.getLocale())
                .withIssuedAt(session.startedAt())
                .withExpiresAt(session.expiredAt())
                .withClaim("tenant.id", tenant.getId().toString())
                .withClaim("tenant.profile", tenant.getName())
                .withClaim("role.id", role.getId())
                .withClaim("role.profile", role.getName())
                .withClaim("permissions", permissionsDetails)
                .sign(algorithm);
    }


    private String refresh(User user, UUID sessionId, Tenant tenant) {
        Session session = user.findSession(tenant.getId(), sessionId).orElseThrow(() -> new SessionDoesNotFoundException(sessionId));
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("token_type", "REFRESH_TOKEN");
        return JWT.create()
                .withHeader(headers)
                .withExpiresAt(session.refreshableUntil())
                .withJWTId(session.id().toString())
                .sign(algorithm);
    }

    @Transactional
    public OAuth refresh(UUID tokenId) {
        User user = userRepository.findBySessionId(tokenId).orElseThrow(() -> new SessionDoesNotFoundException(tokenId));
        Membership membership = user.findMembershipBySessionId(tokenId).orElseThrow();
        user.remove(new SessionRemove(membership.getTenant().getId(), tokenId))
                .throwIfFailure(DomainConstraintViolationException::new);
        userRepository.delete(user, membership, tokenId);
        Session session = user.add(
                        new SessionCreate(
                                membership.getTenant().getId(),
                                tokenConfig.getAccessTokenExpireSeconds(),
                                tokenConfig.getRefreshTokenExpireSeconds()))
                .throwIfFailure(DomainConstraintViolationException::new)
                .value().session();
        userRepository.save(user);
        return OAuth.create(
                access(user, session.id(), membership.getTenant()),
                refresh(user, session.id(), membership.getTenant()),
                session.expiredAt().toEpochMilli(),
                session.refreshableUntil().toEpochMilli()
        );
    }

    public void updateCurrentUserPassword(UserUpdatePasswordCommand command) {
        Long userId = UserLoginInfo.getCurrent().userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!user.matchPassword(passwordEncoder, command.current())) {
            throw new UserPasswordIsNotSameException();
        }
        user.setPassword(passwordEncoder, command.newPassword());
        userRepository.save(user);
    }

    public void updateCurrentProfile(UserUpdateProfileCommand command) {
        updateProfile(UserLoginInfo.getCurrent().userId(), command);
    }

    public void deleteCurrentUser() {
        deleteUser(UserLoginInfo.getCurrent().userId());
    }

    public void leftCurrentUserMembership(Long tenantId) {
        leftMembership(UserLoginInfo.getCurrent().userId(), tenantId);
    }

    public void rejectCurrentUserInvitation(Long tenantId) {
        rejectInvitation(UserLoginInfo.getCurrent().userId(), tenantId);
    }

    public void rejectInvitation(Long userId, Long tenantId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userFillRepository.fillJoinInvitations(user);
        user.rejectJoinInvitation(new UserJoinInvitationReject(tenantId))
                .throwIfFailure(DomainConstraintViolationException::new);
        userRepository.save(user);
    }

    public void acceptCurrentUserInvitation(Long tenantId) {
        Long userId = UserLoginInfo.getCurrent().userId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        userFillRepository.fillJoinInvitations(user);
        user.acceptJoinInvitation(new UserJoinInvitationAccept(tenantId))
                .throwIfFailure(DomainConstraintViolationException::new);
        userRepository.save(user);
    }

    public boolean existsByAuth(String mobile, @Email String email) {
        boolean exists = false;

        if (mobile != null && !mobile.isEmpty()) {
            String normalizedMobile = PhoneNumberHelper.normalize(
                    mobile,
                    LocaleContextHolder.getLocale().getCountry().toUpperCase()
            ).orElseThrow(() -> new IllegalArgumentException("Mobile format is invalid"));

            if (userRepository.existsByMobile(normalizedMobile)) {
                exists = true;
            }
        }

        if (email != null && !email.isEmpty()) {
            if (userRepository.existsByEmail(email)) {
                exists = true;
            }
        }

        return exists;
    }

    public OtpResult sendOtp(String mobile, String email) {
        OtpInfo mobileInfo = null;
        OtpInfo emailInfo = null;
        if (mobile != null && !mobile.isEmpty()) {
            String normalizedMobile = PhoneNumberHelper.normalize(
                    mobile,
                    LocaleContextHolder.getLocale().getCountry().toUpperCase()
            ).orElseThrow(() -> new IllegalArgumentException("Mobile format is invalid"));
            mobileInfo = otpSender.sendOtpToMobile(normalizedMobile);
        }

        if (email != null && !email.isEmpty()) {
            mobileInfo = otpSender.sendOtpToEmail(email);
        }

        return new OtpResult(mobileInfo, emailInfo);
    }
}