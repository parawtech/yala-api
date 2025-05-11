package tech.rket.payment.domain.attempt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, BigInteger> {
    @Query("""
                SELECT a FROM Attempt a
                WHERE
                a.updatedDate <= :lastUpdatedDate
                AND
                 a.status = tech.rket.payment.domain.attempt.AttemptStatus.REDIRECTED
            """)
    Page<Attempt> findAbandonedAttempts(@Param("lastUpdatedDate") Instant instant, Pageable pageable);

    @Query("""
                SELECT a FROM Attempt a
                WHERE
                 a.status = tech.rket.payment.domain.attempt.AttemptStatus.VERIFYING
            """)
    Page<Attempt> findActiveAttempts(Pageable pageable);


    @Query("""
                SELECT a FROM Attempt a
                WHERE
                     a.status in (
                         tech.rket.payment.domain.attempt.AttemptStatus.PAID,
                         tech.rket.payment.domain.attempt.AttemptStatus.FAILED
                     )
                AND
                     a.notifyStatus = tech.rket.payment.domain.attempt.AttemptNotifyStatus.NOTIFYING
            """)
    Page<Attempt> findNotifiableAttempts(Pageable pageable);

    @Query("""
                SELECT a
                FROM Attempt a
                WHERE
                  a.transaction.id = :transactionId
                AND
                  a.status NOT IN (
                    tech.rket.payment.domain.attempt.AttemptStatus.PAID,
                    tech.rket.payment.domain.attempt.AttemptStatus.FAILED
                  )
            """)
    Optional<Attempt> findActiveAttempt(@Param("transactionId") BigInteger transactionId);

    Optional<Attempt> findByReference(Long reference);
}
