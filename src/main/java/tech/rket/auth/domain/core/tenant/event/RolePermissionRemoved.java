package tech.rket.auth.domain.core.tenant.event;

import tech.rket.auth.domain.core.tenant.entity.RolePermission;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record RolePermissionRemoved(Long id, Instant time, RolePermission permission) implements DomainEvent<Long>{
}
