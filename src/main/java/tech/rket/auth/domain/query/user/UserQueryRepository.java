package tech.rket.auth.domain.query.user;

import tech.rket.auth.domain.core.user.User;
import tech.rket.shared.core.query.QueryRepository;

public interface UserQueryRepository extends QueryRepository<User, Long> {
    boolean existsByEmail(String email);
}
