package tech.rket.auth.infrastructure.persistence.permission.impl.mapper;

import org.mapstruct.Mapper;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

@Mapper(config = MapstructConfig.class)
public interface PermissionHibernateMapper extends DomainPersistenceMapper<PermissionEntity, Permission> {
    default PermissionEntity create(Permission permission) {
        return PermissionEntity.build(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }

    default PermissionEntity update(PermissionEntity permissionEntity, Permission permission) {
        permissionEntity.update(permission.getName(), permission.getDescription());
        return permissionEntity;
    }
}
