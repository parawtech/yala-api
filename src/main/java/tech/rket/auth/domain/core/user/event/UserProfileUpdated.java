package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.UserProfile;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserProfileUpdated(Long id, Instant time, UserProfile userProfile, String locale)
        implements DomainEvent<Long> {
}
