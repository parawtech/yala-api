package tech.rket.storage.infrastructure.event.spring;

import tech.rket.shared.core.domain.event.DomainEvent;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.StoredFileEventPublisher;
import tech.rket.shared.infrastructure.log.JDLogger;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoredFileEventSpringPublisher implements StoredFileEventPublisher {
    private static final Logger log = JDLogger.getLogger(StoredFileEventSpringPublisher.class).build();
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(StoredFile aggregate) {
        for (DomainEvent<Long> event : aggregate.getEvents()) {
            eventPublisher.publishEvent(event);
            log.info("Event {} for {} is published.", event, aggregate);
        }
    }
}
