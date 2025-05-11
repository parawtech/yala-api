package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.entity.UserJoinInvitation;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record UserInvited(
        Long id, Instant time,
        User invitee,
        UserJoinInvitation userJoinInvitation
) implements DomainEvent<Long> {
}