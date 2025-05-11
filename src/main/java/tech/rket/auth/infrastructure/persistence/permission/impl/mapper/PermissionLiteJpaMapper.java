package tech.rket.auth.infrastructure.persistence.permission.impl.mapper;

import org.mapstruct.Mapper;
import tech.rket.auth.domain.query.permission.PermissionLite;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
@Mapper(config = MapstructConfig.class)
public interface PermissionLiteJpaMapper extends PersistenceMapper<PermissionEntity, PermissionLite> {
}
