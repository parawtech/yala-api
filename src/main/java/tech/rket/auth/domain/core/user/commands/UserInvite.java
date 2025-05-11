package tech.rket.auth.domain.core.user.commands;

import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.user.User;

import java.time.Instant;

public record UserInvite(
        User inviter,
        Tenant tenant,
        String role,
        Instant expiredAt
) {
}
