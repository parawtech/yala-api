package tech.rket.auth.application.tenant.command;

public record RoleRemovePermissionCommand(String role, String permission) {
}
