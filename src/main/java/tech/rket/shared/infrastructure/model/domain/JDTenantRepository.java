package tech.rket.shared.infrastructure.model.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import tech.rket.shared.infrastructure.model.dto.HasTenant;

import java.util.List;
import java.util.Optional;

public interface JDTenantRepository<T extends HasTenant, ID> {
    @Query("SELECT t FROM #{#entityName} t WHERE t.tenantId = ?1")
    Page<T> findAll(String tenantId, Pageable pageable);

    @Query("SELECT t FROM #{#entityName} t WHERE t.tenantId = ?1")
    List<T> findAll(String tenantId);

    @Query("SELECT t FROM #{#entityName} t WHERE t.tenantId =?1 and t.id=?2")
    Optional<T> findById(String tenantId, ID id);

    @Query("SELECT (count(t) > 0) FROM #{#entityName} t WHERE t.tenantId = ?1 AND t.id = ?2")
    boolean isInTenant(String tenant, ID id);
}
