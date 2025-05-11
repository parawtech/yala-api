package tech.rket.auth.infrastructure.persistence.permission.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.query.permission.PermissionQueryRepository;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.auth.infrastructure.persistence.permission.impl.mapper.PermissionHibernateMapper;
import tech.rket.auth.infrastructure.persistence.permission.repository.PermissionJpaRepository;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class PermissionQueryRepositoryImpl extends SameIdSharedQueryRepository<PermissionEntity, Permission, String>
        implements PermissionQueryRepository {
    private final PermissionJpaRepository repository;
    private final PermissionHibernateMapper mapper;

    @Override
    protected JpaRepository<PermissionEntity, String> getRepository() {
        return repository;
    }

    @Override
    protected PersistenceMapper<PermissionEntity, Permission> getMapper() {
        return mapper;
    }
}
