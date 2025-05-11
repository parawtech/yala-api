package tech.rket.auth.application.tenant.command;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record TenantInviteUserCommand(@NotNull String role, @NotNull String auth, @NotNull Instant expiredAt) {
}
