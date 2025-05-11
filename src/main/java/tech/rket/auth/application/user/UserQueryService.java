package tech.rket.auth.application.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.tenant.info.RoleInfo;
import tech.rket.auth.application.tenant.info.TenantInfo;
import tech.rket.auth.application.user.exception.UserNotFoundException;
import tech.rket.auth.application.user.info.InvitationInfo;
import tech.rket.auth.application.user.info.UserInfo;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserFillRepository;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.query.user.UserLite;
import tech.rket.auth.domain.query.user.UserLiteQueryRepository;
import tech.rket.auth.domain.query.user.UserQueryRepository;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserQueryService {
    private final UserQueryRepository userQueryRepository;
    private final UserLiteQueryRepository userLiteQueryRepository;
    private final UserFillRepository userFillRepository;

    public User get(Long value) {
        return userQueryRepository.findById(value).orElseThrow(() -> new UserNotFoundException(value));
    }

    public boolean existsByAuth(String email) {
        return userQueryRepository.existsByEmail(email);
    }

    public Page<UserLite> getUsers(Pageable pageable) {
        return userLiteQueryRepository.findAll(pageable);
    }

    public UserInfo getUser(Long id) {
        UserLite user = userLiteQueryRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return new UserInfo(id, user.getMobile(), user.getEmail(), user.getName());
    }

    public Set<Membership> getMemberships(Long id) {
        User user = get(id);
        Set<Membership> memberships = new LinkedHashSet<>();
        memberships.add(user.getDefaultMembership());
        memberships.addAll(user.getMemberships());
        return memberships;
    }

    public UserInfo getCurrentUser() {
        return getUser(UserLoginInfo.getCurrent().userId());
    }

    public Set<Membership> getCurrentUserMemberships() {
        return getMemberships(UserLoginInfo.getCurrent().userId());
    }

    public Object getCurrentUserInvites() {
        return getInvites(UserLoginInfo.getCurrent().userId());
    }

    public Set<InvitationInfo> getInvites(Long userId) {
        User user = get(userId);
        userFillRepository.fillJoinInvitations(user);
        return user.getJoinInvitations().stream()
                .map(invitation -> {
                            Role role = invitation.getTenant().getRole(invitation.getRole()).orElseThrow();
                            return new InvitationInfo(
                                    new UserInfo(invitation.getInviter().getId(), invitation.getInviter().getMobile(), invitation.getInviter().getEmail(), invitation.getInviter().getName()),
                                    new TenantInfo(invitation.getTenant().getId(), invitation.getTenant().getName()),
                                    new RoleInfo(role.getId(), role.getName(), role.getDescription()),
                                    invitation.getStatus(),
                                    invitation.getExpiredAt(),
                                    invitation.getCreatedAt(),
                                    invitation.getUpdatedAt()
                            );
                        }
                )
                .collect(Collectors.toSet());
    }
}