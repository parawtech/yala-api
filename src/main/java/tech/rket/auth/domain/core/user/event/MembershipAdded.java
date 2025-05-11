package tech.rket.auth.domain.core.user.event;

import tech.rket.shared.core.domain.event.DomainEvent;
import tech.rket.auth.domain.core.user.entity.Membership;

import java.time.Instant;

public record MembershipAdded(Long id, Instant time, Membership membership)
        implements DomainEvent<Long>{
}
