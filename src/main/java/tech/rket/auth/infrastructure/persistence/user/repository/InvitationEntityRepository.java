package tech.rket.auth.infrastructure.persistence.user.repository;

import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.user.entity.InvitationEntity;

import java.time.Instant;
import java.util.List;

public interface InvitationEntityRepository extends JpaRepository<InvitationEntity, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE InvitationEntity ie set  ie.status = tech.rket.auth.domain.core.user.value_object.InvitationStatus.EXPIRED where ie.expiredAt>=:expiredTime")
    void expireExtinctInvitations(@Param("expiredTime") Instant expiredTime);

    List<InvitationEntity> findAllByUserId(Long id);

    @Query("SELECT i FROM InvitationEntity i WHERE i.role.tenant=:tenant")
    List<InvitationEntity> findAllByTenant(@Param("tenant") TenantEntity tenant);
}
