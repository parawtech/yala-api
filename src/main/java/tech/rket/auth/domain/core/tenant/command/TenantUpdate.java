package tech.rket.auth.domain.core.tenant.command;

import tech.rket.shared.core.domain.command.DomainCommand;

public record TenantUpdate(String name) implements DomainCommand {
}
