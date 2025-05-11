package tech.rket.auth.infrastructure.persistence.user.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.query.user.UserLite;
import tech.rket.auth.domain.query.user.UserLiteQueryRepository;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.auth.infrastructure.persistence.user.impl.mapper.UserLiteQueryMapper;
import tech.rket.auth.infrastructure.persistence.user.repository.UserEntityRepository;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class UserLiteQueryRepositoryImpl extends SameIdSharedQueryRepository<UserEntity, UserLite, Long> implements UserLiteQueryRepository {
    private final UserEntityRepository userEntityRepository;
    private final UserLiteQueryMapper userDomainMapper;

    @Override
    protected JpaRepository<UserEntity, Long> getRepository() {
        return userEntityRepository;
    }

    @Override
    protected PersistenceMapper<UserEntity, UserLite> getMapper() {
        return userDomainMapper;
    }

}
