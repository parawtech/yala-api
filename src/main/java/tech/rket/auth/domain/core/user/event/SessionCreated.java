package tech.rket.auth.domain.core.user.event;

import tech.rket.shared.core.domain.event.DomainEvent;
import tech.rket.auth.domain.core.user.entity.Session;

import java.time.Instant;

public record SessionCreated(Long id, Instant time, Session session)
        implements DomainEvent<Long>{
}
