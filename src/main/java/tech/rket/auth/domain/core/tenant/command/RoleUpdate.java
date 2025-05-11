package tech.rket.auth.domain.core.tenant.command;

import tech.rket.shared.core.domain.command.DomainCommand;

public record RoleUpdate(String identifier, String name, String description) implements DomainCommand {
}
