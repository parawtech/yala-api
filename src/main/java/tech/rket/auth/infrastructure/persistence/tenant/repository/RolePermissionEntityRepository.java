package tech.rket.auth.infrastructure.persistence.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RolePermissionEntity;

public interface RolePermissionEntityRepository extends JpaRepository<RolePermissionEntity, Long> {
    @Transactional
    @Modifying
    @Query(value = """
                    DELETE from RolePermissionEntity rp
                    where
                        rp.role.tenant.id=:tenant and
                        rp.role.identifier=:roleIdentifier and
                        rp.permission.id=:permissionId
            """)
    void delete(@Param("tenant") Long tenantId,
                @Param("roleIdentifier") String roleIdentifier,
                @Param("permissionId") String permissionId);
}
