package tech.rket.storage.infrastructure.persistence;

import io.minio.GetObjectResponse;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;
import tech.rket.shared.infrastructure.persistence.query.SameIdSharedQueryRepository;
import tech.rket.shared.infrastructure.restfilter.util.CriteriaToSpecificationConverter;
import tech.rket.storage.application.exception.StoredFileNotSupportedException;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.entity.StoredFileContent;
import tech.rket.storage.domain.entity.StoredFileTemporaryUrl;
import tech.rket.storage.domain.query.StoredFileQueryRepository;
import tech.rket.storage.domain.query.StoredFileSearchCriteria;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.infrastructure.s3.StorageS3Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class StoredFileQueryRepositoryImpl
        extends SameIdSharedQueryRepository<StoredFileEntity, StoredFile, Long>
        implements StoredFileQueryRepository {
    private final StoredFileEntityRepository repository;
    private final StorageS3Repository s3Repository;
    private final CriteriaToSpecificationConverter criteriaConverter;

    @Override
    protected JpaRepository<StoredFileEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected StoredFileEntityMapper getMapper() {
        return StoredFileEntityMapper.INSTANCE;
    }

    @Override
    public Set<StoredFile> fetchVariants(Long id) {
        return repository.findById(id)
                .map(
                        ent -> repository.findByParent(ent)
                                .stream().map(this.getMapper()::convert)
                                .collect(Collectors.toSet())
                )
                .orElse(Set.of());
    }

    @Override
    public Optional<StoredFile> findByCharacteristicAndKey(Long tenantId, String characteristic, String key) {
        return repository.findByTenantAndCharacteristicEqualsIgnoreCaseAndFileKeyEqualsIgnoreCase(tenantId, characteristic, key)
                .map(this.getMapper()::convert);
    }

    @Override
    public Page<StoredFile> search(StoredFileSearchCriteria criteria, Pageable pageable) {
        String authContains = null;
        if (criteria.getAuthValues() != null) {
            authContains = criteria.getAuthValues().getContains();
            criteria.setAuthValues(null);
        }
        Specification<StoredFileEntity> spec = criteriaConverter.convert(criteria);
        if (authContains != null) {
            final String authValues = authContains;
            Specification<StoredFileEntity> authSpec = ((Root<StoredFileEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
                Path<Instant> authTypePath = root.get("authType");
                Path<Instant> authValuePath = root.get("authValue");
                return cb.or(
                        authTypePath.in("PUBLIC"),
                        authValuePath.in(addPercent(authValues))
                );
            });
            spec.and(authSpec);
        }
        return repository.findAll(spec, pageable)
                .map(this.getMapper()::convert);
    }

    @Override
    public List<Map<String, Object>> findMetadataByIds(List<String> ids) {
        return repository.findMetadataByIds(ids);
    }

    private String addPercent(String authValues) {
        return authValues.replace(",", "%s");
    }

    @Override
    public Optional<StoredFileTemporaryUrl> generateTemporaryUrl(Long id) {
        Optional<StoredFileEntity> entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }
        UserLoginInfo info = UserLoginInfo.findCurrent().orElse(null);
        Long requestedTenantId = info == null ? null : info.tenantId();
        Long requestedUserId = info == null ? null : info.userId();
        StoredFileEntity entity = entityOptional.get();
        StoredFile storedFile = getMapper().convert(entity);
        return Optional.ofNullable(s3Repository.sign(entity.getId(), requestedTenantId, requestedUserId, storedFile.getAuth()));
    }

    @Override
    public Optional<StoredFileContent> fetchContent(Long id) {
        Optional<StoredFileEntity> entityOptional = repository.findById(id);
        if (entityOptional.isEmpty()) {
            return Optional.empty();
        }
        StoredFileEntity entity = entityOptional.get();
        StoredFile storedFile = getMapper().convert(entity);
        GetObjectResponse s3Object = s3Repository.download(storedFile.getAuth(), entity.getId());
        StoredFileContent storedFileContent = new StoredFileContent(
                entity.getId(),
                s3Object.headers().byteCount(),
                MimeType.ofContentType(s3Object.headers().get("CONTENT-TYPE"))
                        .orElseThrow(() -> new StoredFileNotSupportedException(s3Object.headers().get("CONTENT-TYPE"))),
                s3Object);
        return Optional.of(storedFileContent);
    }
}
