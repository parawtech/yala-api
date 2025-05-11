package tech.rket.auth.domain.query.tenant;

import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.shared.core.query.QueryRepository;

public interface TenantQueryRepository extends QueryRepository<Tenant, Long> {
}
