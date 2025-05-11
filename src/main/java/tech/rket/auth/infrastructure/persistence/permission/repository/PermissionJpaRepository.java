package tech.rket.auth.infrastructure.persistence.permission.repository;

import tech.rket.auth.domain.query.permission.PermissionLite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, String> {

    @Query("""
            SELECT new tech.rket.auth.domain.query.permission.PermissionLite(t.id,t.name)
            FROM PermissionEntity t
            where t.id=:id
            """)
    Optional<PermissionLite> findLite(@Param("id") String id);

    @Query("""
            SELECT new tech.rket.auth.domain.query.permission.PermissionLite(t.id,t.name)
            FROM PermissionEntity t
            """)
    Page<PermissionLite> findAllLite(Pageable pageable);

    @Query("""
            SELECT new tech.rket.auth.domain.query.permission.PermissionLite(t.id,t.name)
            FROM PermissionEntity t
            where t.id in :ids
            """)
    Set<PermissionLite> findAllLiteByIds(@Param("ids") Collection<String> ids, Sort sort);

    @Query("""
            SELECT new tech.rket.auth.domain.query.permission.PermissionLite(t.id,t.name)
            FROM PermissionEntity t
            """)
    Set<PermissionLite> findAllLite(Sort sort);
}
