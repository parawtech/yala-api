package tech.rket.auth.infrastructure.persistence.tenant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.query.tenant.TenantLite;
import tech.rket.auth.domain.query.tenant.TenantLiteQueryRepository;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.auth.infrastructure.persistence.tenant.impl.mapper.TenantLitePersistenceMapper;
import tech.rket.auth.infrastructure.persistence.tenant.repository.TenantEntityRepository;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class TenantLiteQueryRepositoryImpl extends SameIdSharedQueryRepository<TenantEntity, TenantLite, Long>
        implements TenantLiteQueryRepository {
    private final TenantEntityRepository tenantEntityRepository;
    private final TenantLitePersistenceMapper mapper;

    @Override
    protected JpaRepository<TenantEntity,Long> getRepository() {
        return tenantEntityRepository;
    }

    @Override
    protected PersistenceMapper<TenantEntity, TenantLite> getMapper() {
        return mapper;
    }
}
