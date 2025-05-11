package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.entity.UserJoinInvitation;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserJoinInvitationRejected(
        Long id, Instant time,
        User user,
        UserJoinInvitation userJoinInvitation
) implements DomainEvent<Long> {
}
