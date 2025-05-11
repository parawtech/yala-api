package tech.rket.storage.domain.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record StoredFileUploaded(Long id, Instant time) implements DomainEvent<Long> {
}
