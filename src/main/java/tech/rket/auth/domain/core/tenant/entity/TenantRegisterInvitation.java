package tech.rket.auth.domain.core.tenant.entity;

import tech.rket.auth.domain.core.user.User;

import java.time.Instant;

public record TenantRegisterInvitation(
        User inviter,
        Role role,
        String auth,
        Instant expiredAt,
        Instant createdAt,
        Instant updatedAt
) {
}
