package tech.rket.storage.domain.event;


import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record StoredFileMarkedForDeleted(Long id, Instant time) implements DomainEvent<Long> {
}
