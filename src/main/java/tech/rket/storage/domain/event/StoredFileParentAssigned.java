package tech.rket.storage.domain.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record StoredFileParentAssigned(Long id, Instant time, Long parentId, String key)
        implements DomainEvent<Long> {
}
