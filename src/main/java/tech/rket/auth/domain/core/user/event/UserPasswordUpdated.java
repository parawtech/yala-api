package tech.rket.auth.domain.core.user.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserPasswordUpdated(Long id, Instant time)
        implements DomainEvent<Long>{
}
