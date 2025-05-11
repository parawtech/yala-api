package tech.rket.auth.application.tenant.command;


public record RoleUpdateCommand(String identifier, String name, String description) {
}
