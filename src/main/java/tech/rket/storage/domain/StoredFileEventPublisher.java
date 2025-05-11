package tech.rket.storage.domain;

import tech.rket.shared.core.domain.event.DomainEventPublisher;

public interface StoredFileEventPublisher extends DomainEventPublisher<StoredFile, Long> {
}
