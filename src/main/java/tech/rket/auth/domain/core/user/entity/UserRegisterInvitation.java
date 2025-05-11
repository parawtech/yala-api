package tech.rket.auth.domain.core.user.entity;

import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;

import java.time.Instant;

public record UserRegisterInvitation(
        User inviter,
        Tenant tenant,
        String role,
        InvitationStatus status,
        Instant expiredAt,
        Instant createdAt,
        Instant updatedAt
) {

}
