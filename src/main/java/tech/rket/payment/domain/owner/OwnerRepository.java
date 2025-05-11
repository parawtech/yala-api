package tech.rket.payment.domain.owner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, BigInteger> {
    Optional<Owner> findByIdentifier(String identifier);
}
