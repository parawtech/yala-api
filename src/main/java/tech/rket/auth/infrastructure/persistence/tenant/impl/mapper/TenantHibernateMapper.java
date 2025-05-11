package tech.rket.auth.infrastructure.persistence.tenant.impl.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.tenant.entity.RolePermission;
import tech.rket.auth.domain.core.tenant.entity.TenantJoinInvitation;
import tech.rket.auth.domain.core.tenant.entity.TenantRegisterInvitation;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.auth.infrastructure.persistence.permission.impl.mapper.PermissionHibernateMapper;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RegisterInvitationEntity;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RolePermissionEntity;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.InvitationEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

@Mapper(config = MapstructConfig.class, uses = {PermissionHibernateMapper.class})
public interface TenantHibernateMapper extends DomainPersistenceMapper<TenantEntity, Tenant> {
    @Mapping(target = "id", source = "identifier")
    Role convert(RoleEntity entity);

    @Override
    @Mapping(target = "registerInvitations", ignore = true)
    Tenant convert(TenantEntity entity);

    default TenantEntity append(TenantEntity tenantEntity,
                                Tenant tenant,
                                Collection<Role> roles,
                                Function<Long, UserEntity> getUser,
                                Function<String, PermissionEntity> getPermission) {
        List<RoleEntity> list = roles.stream()
                .map(r -> tenantEntity.getRoles().stream()
                        .filter(s -> s.getIdentifier().equals(r.getId()))
                        .findFirst()
                        .stream().peek(e -> update(e, r, getPermission)).findFirst()
                        .orElseGet(() -> create(tenantEntity, r, getPermission)))
                .peek(r -> this.appendRegisterInvitations(r, tenant, getUser))
                .toList();
        tenantEntity.getRoles().clear();
        tenantEntity.getRoles().addAll(list);
        return tenantEntity;
    }

    default void appendRegisterInvitations(RoleEntity r, Tenant tenant, Function<Long, UserEntity> getUser) {
        tenant.getRegisterInvitations().stream()
                .filter(e -> e.role().getId().equals(r.getIdentifier()))
                .map(e -> RegisterInvitationEntity.create(e.auth(), r, getUser.apply(e.inviter().getId()), e.expiredAt()))
                .forEach(r.getRegisterInvitations()::add);
    }

    default TenantEntity update(TenantEntity tenantEntity,
                                Tenant tenant,
                                Function<Long, UserEntity> getUser,
                                Function<String, PermissionEntity> getPermission) {
        tenantEntity.update(tenant.getName());
        append(tenantEntity, tenant, tenant.getRoles(), getUser, getPermission);
        return tenantEntity;
    }

    default TenantEntity create(Tenant tenant,
                                Function<Long, UserEntity> getUser,
                                Function<String, PermissionEntity> getPermission) {
        TenantEntity tenantEntity = TenantEntity.build(tenant.getId(), tenant.getWorkDomain(), tenant.getName(), new LinkedHashSet<>());
        append(tenantEntity, tenant, tenant.getRoles(), getUser, getPermission);
        return tenantEntity;
    }

    default RoleEntity update(RoleEntity roleEntity, Role domain, Function<String, PermissionEntity> getPermission) {
        roleEntity.update(domain.getId(), domain.getName(), domain.getDescription());
        if (domain.getPermissions().size() != roleEntity.getPermissions().size()) {
            List<RolePermissionEntity> list = domain.getPermissions().stream()
                    .map(r -> roleEntity.getPermissions().stream()
                            .filter(s -> s.getPermission().getId().equals(r.id()))
                            .findFirst()
                            .orElseGet(() -> create(roleEntity, r, getPermission)))
                    .toList();

            roleEntity.getPermissions().clear();
            roleEntity.getPermissions().addAll(list);
        }
        return roleEntity;
    }

    default RoleEntity create(TenantEntity tenantEntity, Role domain, Function<String, PermissionEntity> getPermission) {
        RoleEntity roleEntity = RoleEntity.build(
                null,
                domain.getId(),
                domain.getName(),
                domain.getDescription(),
                tenantEntity,
                domain.getPermissions() == null ?
                        null :
                        new LinkedHashSet<>());
        if (roleEntity.getPermissions() != null) {
            roleEntity.getPermissions().addAll(domain.getPermissions().stream().map(rp -> create(roleEntity, rp, getPermission)).toList());
        }
        return roleEntity;
    }

    default RolePermissionEntity create(RoleEntity roleEntity, RolePermission rp, Function<String, PermissionEntity> getPermission) {
        return RolePermissionEntity.build(null, roleEntity, getPermission.apply(rp.id()));
    }

    default RolePermission covert(RolePermissionEntity rolePermissionEntity) {
        if (rolePermissionEntity == null || rolePermissionEntity.getPermission() == null) return null;

        return new RolePermission(rolePermissionEntity.getPermission().getId(),
                rolePermissionEntity.getPermission().getName());
    }


    @Mapping(target = "joinInvitations", ignore = true)
    @Mapping(target = "registerInvitations", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @Mapping(target = "password", ignore = true)
    User convert(UserEntity user, Object ignoredAmbitious);

    default TenantJoinInvitation convert(InvitationEntity i) {
        return new TenantJoinInvitation(
                convert(i.getInviter(), null),
                convert(i.getUser(), null),
                convert(i.getRole()),
                i.getStatus(),
                i.getExpiredAt(),
                i.getCreatedDate(),
                i.getUpdatedDate()
        );
    }

    default TenantRegisterInvitation convert(RegisterInvitationEntity entity) {
        return new TenantRegisterInvitation(
                convert(entity.getInviter(), null),
                convert(entity.getRole()),
                entity.getAuth(),
                entity.getExpiredAt(),
                entity.getCreatedDate(),
                entity.getUpdatedDate()
        );
    }

    @Override
    default TenantEntity update(TenantEntity persistedObject, Tenant t) {
        return persistedObject;
    }

    @Override
    default TenantEntity convert(Tenant t) {
        return null;
    }

    @Override
    default TenantEntity create(Tenant t) {
        return DomainPersistenceMapper.super.create(t);
    }
}
