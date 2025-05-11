package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record DefaultMembershipAdded(Long id, Instant time, Membership membership)
        implements DomainEvent<Long> {
}
