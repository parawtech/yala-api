package tech.rket.auth.infrastructure.persistence.user.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserFillRepository;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RegisterInvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.auth.infrastructure.persistence.user.impl.mapper.UserDomainMapper;
import tech.rket.auth.infrastructure.persistence.user.repository.InvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.UserEntityRepository;
import tech.rket.shared.infrastructure.persistence.domain.SharedSameIdDomainRepository;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

@Repository
@RequiredArgsConstructor
public class UserFillRepositoryImpl
        extends SharedSameIdDomainRepository<UserEntity, User, Long>
        implements UserFillRepository {
    private final UserEntityRepository userEntityRepository;
    private final InvitationEntityRepository invitationEntityRepository;
    private final UserDomainMapper userDomainMapper;
    private final RegisterInvitationEntityRepository registerInvitationEntityRepository;

    @Override
    protected JpaRepository<UserEntity, Long> getRepository() {
        return userEntityRepository;
    }

    @Override
    protected DomainPersistenceMapper<UserEntity, User> getMapper() {
        return userDomainMapper;
    }

    @Override
    public void fillJoinInvitations(User user) {
        user.getJoinInvitations()
                .addAll(invitationEntityRepository.findAllByUserId(user.getId()).stream()
                        .map(userDomainMapper::convert)
                        .toList());
    }

    @Override
    public void fillRegisterInvitations(User user) {
        registerInvitationEntityRepository.findByAuthIgnoreCase(user.getEmail()).stream()
                .map(userDomainMapper::convert)
                .forEach(user.getRegisterInvitations()::add);
    }

    @Override
    public Long generateID() {
        return null;
    }
}
