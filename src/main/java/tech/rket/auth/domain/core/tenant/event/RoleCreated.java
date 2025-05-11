package tech.rket.auth.domain.core.tenant.event;

import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record RoleCreated(Long id, Instant time, Role role) implements DomainEvent<Long> {
}
