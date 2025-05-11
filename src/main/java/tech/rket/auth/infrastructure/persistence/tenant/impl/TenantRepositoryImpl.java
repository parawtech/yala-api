package tech.rket.auth.infrastructure.persistence.tenant.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantRepository;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.auth.infrastructure.persistence.permission.repository.PermissionJpaRepository;
import tech.rket.auth.infrastructure.persistence.tenant.entity.TenantEntity;
import tech.rket.auth.infrastructure.persistence.tenant.impl.mapper.TenantHibernateMapper;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RegisterInvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RoleEntityRepository;
import tech.rket.auth.infrastructure.persistence.tenant.repository.RolePermissionEntityRepository;
import tech.rket.auth.infrastructure.persistence.tenant.repository.TenantEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.auth.infrastructure.persistence.user.repository.InvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.UserEntityRepository;
import tech.rket.shared.infrastructure.model.id.JIDGenerator;
import tech.rket.shared.infrastructure.persistence.domain.SharedSameIdDomainRepository;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl extends SharedSameIdDomainRepository<TenantEntity, Tenant, Long> implements TenantRepository {
    private final TenantEntityRepository tenantEntityRepository;
    private final UserEntityRepository userRepository;
    private final RoleEntityRepository roleEntityRepository;
    private final RolePermissionEntityRepository rolePermissionEntityRepository;
    private final TenantHibernateMapper mapper;
    private final PermissionJpaRepository permissionJpaRepository;
    private final RegisterInvitationEntityRepository registerInvitationEntityRepository;

    @Override
    protected JpaRepository<TenantEntity, Long> getRepository() {
        return tenantEntityRepository;
    }

    @Override
    protected DomainPersistenceMapper<TenantEntity, Tenant> getMapper() {
        return mapper;
    }

    @Override
    protected TenantEntity create(Tenant tenant) {
        Set<Role> roles = new HashSet<>(findAllDefaults());
        roles.addAll(tenant.getRoles());
        TenantEntity tenantEntity = mapper.create(tenant, this::getUser, this::getPermission);
        tenantEntity = mapper.append(tenantEntity, tenant, roles, this::getUser, this::getPermission);
        return tenantEntity;
    }

    private PermissionEntity getPermission(String id) {
        return permissionJpaRepository.findById(id).orElseThrow();
    }

    private UserEntity getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }


    @Override
    protected TenantEntity update(TenantEntity tenantEntity, Tenant tenant) {
        tenantEntity = mapper.update(tenantEntity, tenant, this::getUser, this::getPermission);
        return tenantEntity;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> findById(Long s) {
        return tenantEntityRepository.findById(s)
                .map(mapper::convert);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long s) {
        return tenantEntityRepository.existsById(s);
    }

    @Override
    @Transactional
    public void deleteById(Long s) {
        tenantEntityRepository.deleteById(s);
    }

    private Set<Role> findAllDefaults() {
        return roleEntityRepository.findByTenantIsNull(Sort.unsorted()).stream()
                .map(mapper::convert).collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public void delete(Tenant tenant, String roleId) {
        roleEntityRepository.deleteByTenant_IdAndIdentifier(tenant.getId(), roleId);
    }

    @Override
    @Transactional
    public void delete(Tenant tenant, String roleId, String permission) {
        rolePermissionEntityRepository.delete(tenant.getId(), roleId, permission);
    }

    @Override
    public void deleteRegisterInvitation(Long tenantId, String auth) {
        registerInvitationEntityRepository.deleteByAuthIgnoreCaseAndRole_Tenant_Id(auth, tenantId);
    }

    @Override
    public Optional<Tenant> findByWorkDomain(String domainPart) {
        return tenantEntityRepository.findByWorkDomainIgnoreCase(domainPart).map(mapper::convert);
    }


    @Override
    public Long generateID() {
        return JIDGenerator.generate(Instant.now(), (short) 0, (short) 0);
    }
}
