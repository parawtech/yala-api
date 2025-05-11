package tech.rket.auth.domain.core.tenant;


import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import tech.rket.shared.core.domain.repository.DomainRepository;

import java.util.Optional;

public interface TenantRepository extends DomainRepository<Tenant, Long> {
    void delete(Tenant tenant, String roleId);

    void delete(Tenant tenant, String roleId, String permission);

    void deleteRegisterInvitation(Long tenantId, String auth);

    Optional<Tenant> findByWorkDomain(String domainPart);
}
