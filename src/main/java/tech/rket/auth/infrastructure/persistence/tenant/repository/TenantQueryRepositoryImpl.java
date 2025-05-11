package tech.rket.auth.infrastructure.persistence.tenant.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.query.tenant.TenantQueryRepository;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.auth.infrastructure.persistence.tenant.impl.mapper.TenantHibernateMapper;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class TenantQueryRepositoryImpl extends SameIdSharedQueryRepository<TenantEntity, Tenant, Long> implements TenantQueryRepository {
    private final TenantEntityRepository tenantRepository;
    private final TenantHibernateMapper persistenceMapper;

    @Override
    protected JpaRepository<TenantEntity, Long> getRepository() {
        return tenantRepository;
    }

    @Override
    protected PersistenceMapper<TenantEntity, Tenant> getMapper() {
        return persistenceMapper;
    }
}
