package tech.rket.auth.domain.core.tenant.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record TenantUpdated(Long id, Instant time, String name) implements DomainEvent<Long> {
}
