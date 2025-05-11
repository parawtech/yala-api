package tech.rket.auth.infrastructure.persistence.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RegisterInvitationEntity;

import java.util.List;

public interface RegisterInvitationEntityRepository extends JpaRepository<RegisterInvitationEntity, Long> {
    List<RegisterInvitationEntity> findAllByRole_Tenant_Id(Long id);

    List<RegisterInvitationEntity> findByAuthIgnoreCase(String email);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Modifying
    void deleteByAuthIgnoreCaseAndRole_Tenant_Id(String auth, Long tenantId);
}
