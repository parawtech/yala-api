package tech.rket.auth.infrastructure.persistence.tenant.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;

import java.util.List;
import java.util.Optional;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
    List<RoleEntity> findByTenantIsNull(Sort sort);

    @Modifying
    @Transactional
    void deleteByTenant_IdAndIdentifier(Long tenantId, String identifier);

    Optional<RoleEntity> findByTenant_IdAndIdentifier(Long tenantId, String roleId);
}
