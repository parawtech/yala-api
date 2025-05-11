package tech.rket.auth.infrastructure.persistence.user.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantRepository;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserRepository;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RoleEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.auth.infrastructure.persistence.user.impl.mapper.UserDomainMapper;
import tech.rket.auth.infrastructure.persistence.user.repository.MembershipEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.SessionEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.UserEntityRepository;
import tech.rket.shared.infrastructure.model.id.JIDGenerator;
import tech.rket.shared.infrastructure.persistence.domain.SharedSameIdDomainRepository;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl extends SharedSameIdDomainRepository<UserEntity, User, Long> implements UserRepository {
    private final UserEntityRepository userEntityRepository;
    private final MembershipEntityRepository membershipEntityRepository;
    private final SessionEntityRepository sessionEntityRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final UserDomainMapper userDomainMapper;
    private final TenantRepository tenantRepository;

    @Override
    protected JpaRepository<UserEntity, Long> getRepository() {
        return userEntityRepository;
    }

    @Override
    protected DomainPersistenceMapper<UserEntity, User> getMapper() {
        return userDomainMapper;
    }

    @Override
    protected UserEntity create(User value) {
        Tenant tenant = savePersonalTenantIfExists(value.getDefaultMembership().getTenant());
        return userDomainMapper.convert(value, tenant, this::getRole);
    }

    private Tenant savePersonalTenantIfExists(Tenant tenant) {
        if (tenant.getVersion() == null) {
            tenantRepository.save(tenant);
        }
        return tenant;
    }

    @Override
    protected UserEntity update(UserEntity userEntity, User val) {
        return userDomainMapper.update(userEntity, val, l -> userEntityRepository.findById(l).orElseThrow(), this::getRole);
    }

    private RoleEntity getRole(Long tenantId, String roleId) {
        return roleEntityRepository.findByTenant_IdAndIdentifier(tenantId, roleId)
                .orElse(null);
    }

    @Override
    public Long generateID() {
        return JIDGenerator.generate(Instant.now(), (short) 0, (short) 4);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void delete(User user, Membership membership) {
        membershipEntityRepository.deleteByUser_IdAndRole_Tenant_IdAndRole_Identifier(
                user.getId(),
                membership.getTenant().getId(),
                membership.getRole()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void delete(User user, Membership membership, UUID sessionId) {
        sessionEntityRepository.deleteByUniqueId(sessionId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmailIgnoreCase(email).map(userDomainMapper::convert);
    }

    @Override
    public Optional<User> findBySessionId(UUID sessionID) {
        return sessionEntityRepository.findUserBySessionId(sessionID)
                .map(userDomainMapper::convert);
    }

    @Override
    public boolean existsByMobile(String normalizedMobile) {
        return false;
    }
}
