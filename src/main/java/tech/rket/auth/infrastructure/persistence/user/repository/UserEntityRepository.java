package tech.rket.auth.infrastructure.persistence.user.repository;

import tech.rket.auth.domain.query.user.UserLite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
            SELECT new tech.rket.auth.domain.query.user.UserLite(t.id, t.mobile,t.email,t.name)
            FROM UserEntity t
            where t.email=:email
            """)
    Optional<UserLite> findLite(@Param("email") String email);

    @Query("""
            SELECT new tech.rket.auth.domain.query.user.UserLite(t.id, t.mobile, t.email, t.name)
            FROM UserEntity t
            """)
    Page<UserLite> findAllLite(Pageable pageable);

    @Query("""
            SELECT new tech.rket.auth.domain.query.user.UserLite(t.id, t.mobile, t.email, t.name)
            FROM UserEntity t
            where t.email in :email
            """)
    Set<UserLite> findAllLiteByIds(@Param("email") Collection<String> emails, Sort sort);

    @Query("""
            SELECT new tech.rket.auth.domain.query.user.UserLite(t.id, t.mobile, t.email, t.name)
            FROM UserEntity t
            """)
    Set<UserLite> findAllLite(Sort sort);

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
