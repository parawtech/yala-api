package tech.rket.storage.domain.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record StoredFileMetadataAdded(Long id, Instant time, String key, Object value)
        implements DomainEvent<Long> {
}
