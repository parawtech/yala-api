package tech.rket.auth.application.tenant.command;

public record TenantCreateCommand(String identifier, String workDomain, String name) {
}
