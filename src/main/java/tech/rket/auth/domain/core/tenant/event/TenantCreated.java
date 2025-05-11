package tech.rket.auth.domain.core.tenant.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record TenantCreated(Long id, Instant time) implements DomainEvent<Long> {
}
