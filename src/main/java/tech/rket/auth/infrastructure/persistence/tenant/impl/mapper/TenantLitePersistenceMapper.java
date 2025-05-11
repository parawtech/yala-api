package tech.rket.auth.infrastructure.persistence.tenant.impl.mapper;

import org.mapstruct.Mapper;
import tech.rket.auth.domain.query.tenant.TenantLite;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.shared.infrastructure.mapstruct.MapstructConfig;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;

@Mapper(config = MapstructConfig.class)
public interface TenantLitePersistenceMapper extends PersistenceMapper<TenantEntity, TenantLite> {
}
