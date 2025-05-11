package tech.rket.auth.domain.core.permission.command;

import tech.rket.shared.core.domain.command.DomainCommand;

public record PermissionUpdate(String name, String description) implements DomainCommand {
}
