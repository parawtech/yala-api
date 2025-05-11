package tech.rket.auth.infrastructure.persistence.tenant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.rket.auth.domain.query.tenant.TenantLite;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface TenantEntityRepository extends JpaRepository<TenantEntity, Long> {
    @Query("""
            SELECT new tech.rket.auth.domain.query.tenant.TenantLite(t.id,t.name)
            FROM TenantEntity t
            where t.id=:id
            """)
    Optional<TenantLite> findLite(@Param("id") Long id);

    @Query("""
            SELECT new tech.rket.auth.domain.query.tenant.TenantLite(t.id,t.name)
            FROM TenantEntity t
            """)
    Page<TenantLite> findAllLite(Pageable pageable);

    @Query("""
            SELECT new tech.rket.auth.domain.query.tenant.TenantLite(t.id,t.name)
            FROM TenantEntity t
            where t.id in :ids
            """)
    Set<TenantLite> findAllLiteByIds(@Param("ids") Collection<Long> ids, Sort sort);

    @Query("""
            SELECT new tech.rket.auth.domain.query.tenant.TenantLite(t.id,t.name)
            FROM TenantEntity t
            """)
    Set<TenantLite> findAllLite(Sort sort);

    Optional<TenantEntity> findByWorkDomainIgnoreCase(String workDomain);
}
