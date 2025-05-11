package tech.rket.auth.domain.core.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tech.rket.auth.domain.core.service.AuthInvitationPredicate;
import tech.rket.auth.domain.core.service.UserPasswordEncoder;
import tech.rket.auth.domain.core.user.commands.*;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.core.user.entity.Session;
import tech.rket.auth.domain.core.user.entity.UserJoinInvitation;
import tech.rket.auth.domain.core.user.entity.UserRegisterInvitation;
import tech.rket.auth.domain.core.user.event.*;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;
import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.domain.SharedAggregateRoot;
import tech.rket.shared.core.domain.result.DomainResult;
import tech.rket.shared.core.query.QueryObject;

import java.time.Instant;
import java.util.*;

import static tech.rket.auth.domain.core.user.UserConstraintViolation.USER_CAN_NOT_BE_INVITED;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class User extends SharedAggregateRoot<Long>
        implements DomainObject.Entity.AggregateRoot<Long>, QueryObject<Long> {
    private Long id;
    private String mobile;
    private String email;
    private String name;
    private String password;
    private String locale;
    private Membership defaultMembership;
    private Instant createdDate;
    private Instant updatedDate;
    private Instant deletedDate;
    private UserProfile userProfile;
    private final Set<Membership> memberships = new HashSet<>();
    private final Set<UserJoinInvitation> joinInvitations = new HashSet<>();
    private final Set<UserRegisterInvitation> registerInvitations = new HashSet<>();

    public static DomainResult<User> create(UserCreate command) {
        User user = new User(command.id(), command.mobile(), command.email(), null, null, command.locale(), null, null, null, null, null);
        var event = new UserCreated(command.id(), Instant.now(), command.mobile(), command.email(), command.locale());
        user.registerEvent(event);
        return DomainResult.success(user);
    }

    public DomainResult<DefaultMembershipAdded> addDefaultMembership(DefaultMembershipAdd command) {
        if (find(command.tenant().getId()).isPresent()) {
            return DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_EXIST_ALREADY);
        }
        Membership membership = Membership.build(command.tenant(), command.roleId());
        defaultMembership = membership;
        var event = new DefaultMembershipAdded(this.getId(), Instant.now(), membership);
        registerEvent(event);
        return DomainResult.success(event);
    }

    public boolean isDefaultMembership(Long tenantId) {
        return defaultMembership != null && defaultMembership.getTenant().getId().equals(tenantId);
    }

    public DomainResult<UserProfileUpdated> updateProfile(UserProfileUpdate command) {
        if (command.profile() != null) {
            this.userProfile = command.profile();
            this.name = command.profile().toName();
        }
        if (command.locale() != null) {
            this.locale = command.locale();
        }
        return DomainResult.success(new UserProfileUpdated(this.getId(), Instant.now(), command.profile(), command.locale()))
                .peek(this::registerEvent);
    }

    public DomainResult<UserPasswordUpdated> setPassword(UserPasswordEncoder encoder, String rawPassword) {
        password = encoder.encodePassword(rawPassword);
        return DomainResult.success(new UserPasswordUpdated(this.getId(), Instant.now()))
                .peek(this::registerEvent);
    }

    public boolean matchPassword(UserPasswordEncoder encoder, String rawPassword) {
        return encoder.matches(rawPassword, password);
    }

    public DomainResult<UserDisabled> disable() {
        this.deletedDate = Instant.now();
        return DomainResult.success(new UserDisabled(this.getId(), Instant.now()))
                .peek(this::registerEvent);
    }

    public DomainResult<MembershipAdded> add(MembershipAdd command) {
        if (isDefaultMembership(command.tenant().getId()) ||
                find(command.tenant().getId()).isPresent()) {
            return DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_EXIST_ALREADY);
        }
        Membership membership = Membership.build(command.tenant(), command.role());
        memberships.add(membership);
        return DomainResult.success(new MembershipAdded(this.getId(), Instant.now(), membership))
                .peek(this::registerEvent);
    }


    public Optional<Membership> find(Long tenantId) {
        if (defaultMembership != null && defaultMembership.getTenant().getId().equals(tenantId)) {
            return Optional.of(defaultMembership);
        }
        return memberships.stream().filter(f -> f.getTenant().getId().equals(tenantId)).findAny();
    }

    public DomainResult<MembershipRemoved> remove(MembershipRemove command) {
        return find(command.tenantId()).stream()
                .peek(memberships::remove).findFirst()
                .map(r -> new MembershipRemoved(this.getId(), Instant.now(), r))
                .map(DomainResult::success)
                .orElseGet(() -> DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_NOT_FOUND))
                .peek(this::registerEvent);
    }

    public Optional<Membership> findMembershipBySessionId(UUID sessionId) {
        if (defaultMembership != null && defaultMembership.findSession(sessionId).isPresent()) {
            return Optional.of(defaultMembership);
        }
        return memberships.stream().filter(f -> f.findSession(sessionId).isPresent()).findAny();
    }

    public Optional<Session> findSession(Long tenantId, UUID sessionId) {
        return find(tenantId)
                .flatMap(m -> m.findSession(sessionId));
    }

    public DomainResult<SessionCreated> add(SessionCreate sessionCreate) {
        return find(sessionCreate.tenantId())
                .map(m -> m.createSession(sessionCreate))
                .map(r -> new SessionCreated(this.getId(), Instant.now(), r))
                .map(DomainResult::success)
                .orElseGet(() -> DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_NOT_FOUND))
                .peek(this::registerEvent);
    }

    public DomainResult<SessionRemoved> remove(SessionRemove sessionRemove) {
        Optional<Membership> membershipOptional = find(sessionRemove.tenantId());
        if (membershipOptional.isEmpty()) {
            return DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_NOT_FOUND);
        }
        Membership membership = membershipOptional.get();
        return membership.removeSession(sessionRemove.sessionId())
                .map(r -> new SessionRemoved(this.getId(), Instant.now(), r))
                .map(DomainResult::success)
                .orElseGet(() -> DomainResult.fail(UserConstraintViolation.MEMBERSHIP_DOES_NOT_FOUND))
                .peek(this::registerEvent);
    }

    public DomainResult<UserInvited> inviteJoin(AuthInvitationPredicate authInvitationPredicate, UserJoinInvite command) {
        Optional<UserJoinInvitation> invitationOptional = findLastJoinInvitation(command.tenant().getId());
        if (invitationOptional.isPresent()) {
            if (invitationOptional.get().getStatus() == InvitationStatus.PENDING) {
                return DomainResult.fail(UserConstraintViolation.INVITE_PENDING_DOES_EXISTS_ALREADY);
            } else if (invitationOptional.get().getStatus() == InvitationStatus.ACCEPTED) {
                return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
            } else if (invitationOptional.get().getStatus() == InvitationStatus.REVOKED) {
                return DomainResult.fail(UserConstraintViolation.INVITE_IS_REVOKED_ALREADY);
            }
        }
        if (!authInvitationPredicate.canBeInvited(command.tenant(), command.role(), this.email)) {
            return DomainResult.fail(USER_CAN_NOT_BE_INVITED);
        }

        UserJoinInvitation userJoinInvitation = new UserJoinInvitation(
                command.inviter(),
                command.tenant(),
                command.role(),
                InvitationStatus.PENDING,
                command.expiredAt(),
                null, null, null);

        joinInvitations.add(userJoinInvitation);

        return DomainResult.success(new UserInvited(this.getId(), Instant.now(), this, userJoinInvitation))
                .peek(this::registerEvent);
    }

    public DomainResult<UserJoinInvitationRevoked> revokeJoinInvitation(UserJoinInvitationRevoke command) {
        Optional<UserJoinInvitation> invitationOptional = findLastJoinInvitation(command.tenantId());
        if (invitationOptional.isEmpty()) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        }
        UserJoinInvitation userJoinInvitation = invitationOptional.get();
        if (userJoinInvitation.getStatus() == InvitationStatus.REVOKED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REVOKED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.ACCEPTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.REJECTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REJECTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.EXPIRED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_EXPIRED_ALREADY);
        }
        userJoinInvitation.setStatus(InvitationStatus.REVOKED);

        return DomainResult.success(new UserJoinInvitationRevoked(
                        this.getId(), Instant.now(),
                        this,
                        userJoinInvitation))
                .peek(this::registerEvent);
    }

    public DomainResult<UserJoinInvitationAccepted> acceptJoinInvitation(UserJoinInvitationAccept command) {
        Optional<UserJoinInvitation> invitationOptional = findLastJoinInvitation(command.tenantId());
        if (invitationOptional.isEmpty()) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        }
        UserJoinInvitation userJoinInvitation = invitationOptional.get();
        if (userJoinInvitation.getStatus() == InvitationStatus.REVOKED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REVOKED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.ACCEPTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.REJECTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REJECTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.EXPIRED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_EXPIRED_ALREADY);
        } else if (userJoinInvitation.getExpiredAt().isBefore(Instant.now())) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_EXPIRED_ALREADY);
        }
        userJoinInvitation.setStatus(InvitationStatus.ACCEPTED);
        DomainResult<MembershipAdded> membershipAdded = add(new MembershipAdd(userJoinInvitation.getTenant(), userJoinInvitation.getRole()));

        if (membershipAdded.isFailure()) {
            return DomainResult.fail(membershipAdded.violations());
        }

        return DomainResult.success(new UserJoinInvitationAccepted(
                this.getId(), Instant.now(),
                this,
                userJoinInvitation,
                membershipAdded.value().membership()
        )).peek(this::registerEvent);
    }

    public DomainResult<UserJoinInvitationRejected> rejectJoinInvitation(UserJoinInvitationReject command) {
        Optional<UserJoinInvitation> invitationOptional = findLastJoinInvitation(command.tenantId());
        if (invitationOptional.isEmpty()) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        }
        UserJoinInvitation userJoinInvitation = invitationOptional.get();
        if (userJoinInvitation.getStatus() == InvitationStatus.REVOKED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REVOKED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.ACCEPTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_ACCEPTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.REJECTED) {
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_REJECTED_ALREADY);
        } else if (userJoinInvitation.getStatus() == InvitationStatus.EXPIRED || userJoinInvitation.isExpired()) {
            userJoinInvitation.setStatus(InvitationStatus.EXPIRED);
            return DomainResult.fail(UserConstraintViolation.INVITE_IS_EXPIRED_ALREADY);
        }

        userJoinInvitation.setStatus(InvitationStatus.REJECTED);
        return DomainResult.success(new UserJoinInvitationRejected(
                this.getId(), Instant.now(),
                this,
                userJoinInvitation
        )).peek(this::registerEvent);
    }

    public Optional<UserJoinInvitation> findLastJoinInvitation(Long tenantId) {
        return joinInvitations.stream()
                .filter(e -> e.getTenant().getId().equals(tenantId))
                .max(Comparator.comparing(UserJoinInvitation::getCreatedAt));
    }
}