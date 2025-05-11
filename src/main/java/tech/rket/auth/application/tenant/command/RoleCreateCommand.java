package tech.rket.auth.application.tenant.command;


public record RoleCreateCommand(String identifier, String name, String description) {
}
