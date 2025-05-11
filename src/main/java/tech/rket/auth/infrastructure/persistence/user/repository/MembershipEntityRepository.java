package tech.rket.auth.infrastructure.persistence.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.user.entity.MembershipEntity;

public interface MembershipEntityRepository extends JpaRepository<MembershipEntity, Long> {
    @Transactional
    @Modifying
    void deleteByUser_IdAndRole_Tenant_IdAndRole_Identifier(Long id, Long id1, String role);
}
