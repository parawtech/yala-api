package tech.rket.auth.domain.core.user.event;

import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserCreated(Long id, Instant time, String mobile, String email,
                          String locale) implements DomainEvent<Long> {
}
