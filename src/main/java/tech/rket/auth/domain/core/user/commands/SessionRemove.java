package tech.rket.auth.domain.core.user.commands;

import tech.rket.shared.core.domain.command.DomainCommand;

import java.util.UUID;

public record SessionRemove(Long tenantId, UUID sessionId)
        implements DomainCommand {
}
