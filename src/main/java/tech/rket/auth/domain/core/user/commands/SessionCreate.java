package tech.rket.auth.domain.core.user.commands;

import tech.rket.shared.core.domain.command.DomainCommand;

public record SessionCreate(Long tenantId, long accessTokenExpireSeconds, long refreshTokenExpireSeconds)
        implements DomainCommand {
}
