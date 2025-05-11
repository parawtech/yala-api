package tech.rket.auth.infrastructure.persistence.permission.impl.mapper;

import org.mapstruct.Mapper;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

@Mapper(config = MapstructConfig.class)
public interface PermissionRepositoryMapper extends DomainPersistenceMapper<PermissionEntity, Permission> {
}
