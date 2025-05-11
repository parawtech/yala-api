package tech.rket.auth.domain.core.permission.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record PermissionCreated(String id, Instant time, String name,
                                String description) implements DomainEvent<String> {
}
