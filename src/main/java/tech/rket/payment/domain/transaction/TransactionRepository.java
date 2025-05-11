package tech.rket.payment.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, BigInteger> {
    Optional<Transaction> findByOwner_IdentifierAndReference(String ownerIdentifier, String reference);

    @Query("""
            SELECT t
            from Transaction  t
            where
                t.owner.identifier=:ownerIdentifier
            AND
                t.reference=:reference
            and t.status in (
                tech.rket.payment.domain.transaction.TransactionStatus.FAILED,
                tech.rket.payment.domain.transaction.TransactionStatus.INITIATED
            )
            """)
    Optional<Transaction> findActiveTransaction(@Param("ownerIdentifier") String ownerIdentifier, @Param("reference") String reference);
}
