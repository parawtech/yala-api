package tech.rket.auth.domain.core.tenant.command;

import tech.rket.auth.domain.core.user.User;

import java.time.Instant;

public record TenantRegisterInvite(User inviter, String role, String auth, Instant expiredAt) {
}
