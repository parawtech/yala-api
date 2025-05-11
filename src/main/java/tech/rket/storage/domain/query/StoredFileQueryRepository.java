package tech.rket.storage.domain.query;

import tech.rket.shared.core.query.QueryRepository;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.entity.StoredFileContent;
import tech.rket.storage.domain.entity.StoredFileTemporaryUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StoredFileQueryRepository extends QueryRepository<StoredFile, Long> {
    Optional<StoredFileTemporaryUrl> generateTemporaryUrl(Long id);

    Optional<StoredFileContent> fetchContent(Long id);

    Set<StoredFile> fetchVariants(Long id);

    Optional<StoredFile> findByCharacteristicAndKey(Long tenantId, String characteristic, String key);

    Page<StoredFile> search(StoredFileSearchCriteria criteria, Pageable pageable);

    List<Map<String, Object>> findMetadataByIds(List<String> ids);
}
