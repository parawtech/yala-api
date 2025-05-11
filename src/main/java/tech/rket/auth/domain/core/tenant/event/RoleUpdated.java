package tech.rket.auth.domain.core.tenant.event;

import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record RoleUpdated(Long id, Instant time, Role value) implements DomainEvent<Long>{
}
