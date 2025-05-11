package tech.rket.auth.domain.core.user;

import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.shared.core.domain.repository.DomainRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends DomainRepository<User, Long> {

    boolean existsByEmail(String email);

    void delete(User user, Membership membership);

    void delete(User user, Membership membership, UUID sessionId);

    Optional<User> findByEmail(String email);

    Optional<User> findBySessionId(UUID sessionID);

    boolean existsByMobile(String normalizedMobile);
}
