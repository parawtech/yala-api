package tech.rket.auth.infrastructure.persistence.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.infrastructure.persistence.user.entity.SessionEntity;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SessionEntityRepository extends JpaRepository<SessionEntity, Long> {
    @Transactional
    @Modifying
    void deleteByRefreshableUntilIsLessThan(Instant now);

    @Transactional
    @Modifying
    void deleteByUniqueId(UUID sessionId);

    @Query("SELECT s.membership.user from SessionEntity  s where s.uniqueId=:uniqueId")
    Optional<UserEntity> findUserBySessionId(@Param("uniqueId") UUID uniqueId);
}
