package tech.rket.auth.infrastructure.persistence.permission.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.core.permission.PermissionRepository;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.auth.infrastructure.persistence.permission.repository.PermissionJpaRepository;
import tech.rket.auth.infrastructure.persistence.permission.impl.mapper.PermissionHibernateMapper;
import tech.rket.shared.infrastructure.persistence.domain.SharedSameIdDomainRepository;
import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryImpl extends SharedSameIdDomainRepository<PermissionEntity, Permission, String>
        implements PermissionRepository {
    private final PermissionJpaRepository repository;
    private final PermissionHibernateMapper mapper;

    @Override
    protected JpaRepository<PermissionEntity, String> getRepository() {
        return repository;
    }

    @Override
    protected DomainPersistenceMapper<PermissionEntity, Permission> getMapper() {
        return mapper;
    }

    @Override
    public String generateID() {
        return "";
    }
}
