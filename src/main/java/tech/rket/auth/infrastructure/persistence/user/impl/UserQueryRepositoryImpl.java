package tech.rket.auth.infrastructure.persistence.user.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.query.user.UserQueryRepository;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.auth.infrastructure.persistence.user.impl.mapper.UserDomainMapper;
import tech.rket.auth.infrastructure.persistence.user.repository.UserEntityRepository;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl extends SameIdSharedQueryRepository<UserEntity, User, Long> implements UserQueryRepository {
    private final UserEntityRepository userEntityRepository;
    private final UserDomainMapper userDomainMapper;

    @Override
    protected JpaRepository<UserEntity, Long> getRepository() {
        return userEntityRepository;
    }

    @Override
    protected PersistenceMapper<UserEntity, User> getMapper() {
        return userDomainMapper;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmailIgnoreCase(email);
    }
}
