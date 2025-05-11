package tech.rket.auth.infrastructure.persistence.user.impl.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.core.user.entity.Session;
import tech.rket.auth.domain.core.user.entity.UserJoinInvitation;
import tech.rket.auth.domain.core.user.entity.UserRegisterInvitation;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RegisterInvitationEntity;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;
import tech.rket.auth.infrastructure.persistence.tenant.impl.mapper.TenantHibernateMapper;
import tech.rket.auth.infrastructure.persistence.user.entity.InvitationEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.MembershipEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.SessionEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(config = MapstructConfig.class, uses = {TenantHibernateMapper.class})
public interface UserDomainMapper extends DomainPersistenceMapper<UserEntity, User> {
    static ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();

    default UserEntity update(UserEntity userEntity, User domain, Function<Long, UserEntity> userEntityFunction, BiFunction<Long, String, RoleEntity> roleFunction) {
        userEntity.update(domain.getPassword(), OBJECT_MAPPER.convertValue(domain.getUserProfile(), new TypeReference<>() {
        }), domain.getLocale());
        appendDefaultMembership(userEntity, domain.getDefaultMembership().getTenant(), domain, roleFunction);
        appendMembership(userEntity, domain, roleFunction);
        appendInvitations(userEntity, domain, userEntityFunction, roleFunction);
        return userEntity;
    }

    @Override
    default UserEntity update(UserEntity persistedObject, User t) {
        return persistedObject;
    }

    default SessionEntity create(Session session, MembershipEntity entity) {
        return SessionEntity.build(
                session.id(),
                entity,
                session.startedAt(),
                session.expiredAt(),
                session.refreshableUntil()
        );
    }

    default UserEntity convert(User domain, Tenant tenant, BiFunction<Long, String, RoleEntity> roleFunction) {
        UserEntity userEntity = UserEntity.build(
                domain.getId(),
                domain.getEmail(),
                domain.getMobile(),
                domain.getPassword(),
                domain.getLocale()
        );
        appendDefaultMembership(userEntity, tenant, domain, roleFunction);
        appendMembership(userEntity, domain, roleFunction);
        return userEntity;
    }

    default void appendDefaultMembership(UserEntity userEntity, Tenant tenant, User domain, BiFunction<Long, String, RoleEntity> roleFunction) {
        Optional<MembershipEntity> defaultMembership = userEntity.getMemberships().stream()
                .filter(MembershipEntity::isDefault).findFirst();

        if (defaultMembership.isPresent()) {
            MembershipEntity existingMembership = defaultMembership.get();
            if (!isSameMembership(existingMembership, domain.getDefaultMembership())) {
                userEntity.getMemberships().remove(existingMembership);
                userEntity.getMemberships().add(create(userEntity, domain.getDefaultMembership(), roleFunction, true));
            }
            appendSessions(existingMembership, domain.getDefaultMembership());
        } else {
            Membership defaultMembershipDomain = new Membership(tenant, domain.getDefaultMembership().getRole(), domain.getDefaultMembership().getSessions());
            userEntity.getMemberships().add(create(userEntity, defaultMembershipDomain, roleFunction, true));
        }
    }

    default void appendMembership(UserEntity entity, User user, BiFunction<Long, String, RoleEntity> roleFunction) {
        List<MembershipEntity> list = user.getMemberships().stream()
                .map(me -> entity.getMemberships().stream()
                        .filter(s -> isSameMembership(s, me))
                        .findFirst()
                        .orElseGet(() -> create(entity, me, roleFunction, false)))
                .peek(eme -> {
                    Membership membership = user.getMemberships().stream()
                            .filter(s -> isSameMembership(eme, s))
                            .findFirst()
                            .orElseThrow();
                    appendSessions(eme, membership);
                })
                .toList();
        entity.getMemberships().removeIf(s -> !s.isDefault());
        entity.getMemberships().addAll(list);
    }

    default boolean isSameMembership(MembershipEntity s, Membership me) {
        return s.getRole().getTenant().getId().equals(me.getTenant().getId()) && s.getRole().getIdentifier().equals(me.getRole());
    }

    default MembershipEntity create(UserEntity userEntity, Membership domain, BiFunction<Long, String, RoleEntity> roleFunction, boolean isDefault) {
        MembershipEntity entity = MembershipEntity.build(
                userEntity,
                roleFunction.apply(domain.getTenant().getId(), domain.getRole()),
                isDefault
        );
        appendSessions(entity, domain);
        return entity;
    }

    default void appendSessions(MembershipEntity entity, Membership domain) {
        List<SessionEntity> list = domain.getSessions().stream()
                .map(session -> entity.getSessions().stream()
                        .filter(s -> isSameSession(s, session))
                        .findFirst()
                        .orElseGet(() -> create(entity, session)))
                .toList();
        entity.getSessions().clear();
        entity.getSessions().addAll(list);
    }

    default SessionEntity create(MembershipEntity membershipEntity, Session domain) {
        return SessionEntity.build(
                domain.id(),
                membershipEntity,
                domain.startedAt(),
                domain.expiredAt(),
                domain.refreshableUntil()
        );
    }

    default boolean isSameSession(SessionEntity sessionEntity, Session session) {
        return sessionEntity.getUniqueId().equals(session.id());
    }

    @Override
    @Mapping(source = "memberships", target = "defaultMembership", qualifiedByName = "getDefaultMembership")
    @Mapping(source = "memberships", target = "memberships", qualifiedByName = "getNonDefaultMembership")
    User convert(UserEntity entity);

    @Mapping(target = "tenant", source = "role.tenant")
    @Mapping(target = "role", source = "role.identifier")
    Membership convert(MembershipEntity membershipEntity);

    @Mapping(source = "uniqueId", target = "id")
    Session convert(SessionEntity session);

    @Named("getDefaultMembership")
    default Membership getDefaultMembership(Set<MembershipEntity> membershipEntities) {
        return membershipEntities.stream()
                .filter(MembershipEntity::isDefault).findFirst()
                .map(this::convert)
                .orElse(null);
    }

    @Named("getNonDefaultMembership")
    default Set<Membership> getNonDefaultMembership(Set<MembershipEntity> membershipEntities) {
        return membershipEntities.stream()
                .filter(s -> !s.isDefault())
                .map(this::convert)
                .collect(Collectors.toSet());
    }

    @Mapping(source = "role.identifier", target = "role")
    @Mapping(source = "role.tenant", target = "tenant")
    UserJoinInvitation convert(InvitationEntity invitationEntity);

    default void appendInvitations(UserEntity userEntity, User domain, Function<Long, UserEntity> userEntityFunction, BiFunction<Long, String, RoleEntity> roleFunction) {
        Function<UserJoinInvitation, Optional<InvitationEntity>> findPendingButChangedOne = (invitation) ->
                userEntity.getInvitations().stream()
                        .filter(ie ->
                                ie.getVersion() != null &&
                                        ie.getRole().getTenant().getId().equals(invitation.getTenant().getId()) &&
                                        ie.getStatus() == InvitationStatus.PENDING && ie.getStatus() != invitation.getStatus())
                        .findAny();

        List<InvitationEntity> newInvitations = domain.getJoinInvitations().stream()
                .filter(i -> i.getVersion() == null)
                .map(e -> InvitationEntity.build(
                        userEntity,
                        userEntityFunction.apply(e.getInviter().getId()),
                        roleFunction.apply(e.getTenant().getId(), e.getRole()),
                        e.getStatus(),
                        e.getExpiredAt()
                ))
                .toList();

        List<InvitationEntity> changedStatusInvitations = domain.getJoinInvitations().stream()
                .map(i -> findPendingButChangedOne.apply(i)
                        .map(invitation -> {
                            invitation.setStatus(i.getStatus());
                            return invitation;
                        }))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        List<InvitationEntity> oldOneInvitations = userEntity.getInvitations().stream()
                .filter(i -> changedStatusInvitations.stream()
                        .noneMatch(a -> a.getId().equals(i.getId())))
                .toList();

        userEntity.getInvitations().clear();
        userEntity.getInvitations().addAll(oldOneInvitations);
        userEntity.getInvitations().addAll(changedStatusInvitations);
        userEntity.getInvitations().addAll(newInvitations);
    }

    @Mapping(source = "role.identifier", target = "role")
    @Mapping(source = "role.tenant", target = "tenant")
    UserRegisterInvitation convert(RegisterInvitationEntity registerInvitationEntity);

    @Override
    default UserEntity convert(User t) {
        return null;
    }

    @Override
    default UserEntity create(User t) {
        return null;
    }
}
