package tech.rket.auth.domain.core.permission.command;

import tech.rket.shared.core.domain.command.DomainCommand;

public record PermissionCreate(String id, String name, String description) implements DomainCommand {
}
