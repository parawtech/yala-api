package tech.rket.auth.infrastructure.persistence.permission.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.auth.domain.query.permission.PermissionLite;
import tech.rket.auth.domain.query.permission.PermissionLiteQueryRepository;
import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.auth.infrastructure.persistence.permission.repository.PermissionJpaRepository;
import tech.rket.auth.infrastructure.persistence.permission.impl.mapper.PermissionLiteJpaMapper;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;

@Repository
@RequiredArgsConstructor
public class PermissionLiteRepositoryImpl extends SameIdSharedQueryRepository<PermissionEntity, PermissionLite, String>
        implements PermissionLiteQueryRepository {
    private final PermissionJpaRepository repository;
    private final PermissionLiteJpaMapper mapper;

    @Override
    protected JpaRepository<PermissionEntity, String> getRepository() {
        return repository;
    }

    @Override
    protected PersistenceMapper<PermissionEntity, PermissionLite> getMapper() {
        return mapper;
    }
}
