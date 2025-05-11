package tech.rket.storage.infrastructure.persistence;

import tech.rket.shared.infrastructure.persistence.domain.SharedSameIdDomainRepository;
import tech.rket.shared.infrastructure.persistence.shared.JIDGeneratorByClassAnnotation;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.StoredFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class StoredFileJpaDomainRepository
        extends SharedSameIdDomainRepository<StoredFileEntity, StoredFile, Long>
        implements StoredFileRepository {
    private final StoredFileEntityRepository repository;

    @Override
    public Long generateID() {
        return JIDGeneratorByClassAnnotation.generate(StoredFileEntity.class);
    }

    @Override
    protected JpaRepository<StoredFileEntity, Long> getRepository() {
        return repository;
    }

    @Override
    protected StoredFileEntityMapper getMapper() {
        return StoredFileEntityMapper.INSTANCE;
    }
}
