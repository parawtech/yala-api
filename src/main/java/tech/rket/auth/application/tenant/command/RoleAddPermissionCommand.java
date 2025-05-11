package tech.rket.auth.application.tenant.command;

public record RoleAddPermissionCommand(String role, String permission) {
}
