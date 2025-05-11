package tech.rket.storage.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StoredFileEntityRepository extends JpaRepository<StoredFileEntity, Long>, JpaSpecificationExecutor<StoredFileEntity> {
    Set<StoredFileEntity> findByParent(StoredFileEntity parent);

    Optional<StoredFileEntity> findByTenantAndCharacteristicEqualsIgnoreCaseAndFileKeyEqualsIgnoreCase(Long tenantId, String characteristic, String key);

    @Transactional
    @Modifying
    @Query("select  t.metadataList from StoredFileEntity  t where t.id in (:ids) ")
    List<Map<String, Object>> findMetadataByIds(@Param("ids") List<String> ids);
}
