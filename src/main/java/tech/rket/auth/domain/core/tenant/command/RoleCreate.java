package tech.rket.auth.domain.core.tenant.command;

import tech.rket.shared.core.domain.command.DomainCommand;

public record RoleCreate(String id, String name, String description) implements DomainCommand {
}
