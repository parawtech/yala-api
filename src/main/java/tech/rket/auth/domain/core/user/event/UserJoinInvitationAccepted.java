package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.core.user.entity.UserJoinInvitation;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserJoinInvitationAccepted(
        Long id, Instant time,
        User user,
        UserJoinInvitation userJoinInvitation,
        Membership membership
) implements DomainEvent<Long> {
}
