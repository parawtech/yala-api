package tech.rket.auth.domain.query.permission;

import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.shared.core.query.QueryRepository;

public interface PermissionQueryRepository extends QueryRepository<Permission, String> {
}
