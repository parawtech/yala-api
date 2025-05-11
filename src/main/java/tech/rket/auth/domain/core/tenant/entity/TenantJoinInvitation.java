package tech.rket.auth.domain.core.tenant.entity;

import tech.rket.auth.domain.core.user.value_object.InvitationStatus;
import tech.rket.auth.domain.core.user.User;

import java.time.Instant;

public record TenantJoinInvitation(
        User inviter,
        User invitee,
        Role role,
        InvitationStatus status,
        Instant expiredAt,
        Instant createdAt,
        Instant updatedAt) {
}
