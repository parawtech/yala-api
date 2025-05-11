package tech.rket.auth.domain.core.user.commands;

import tech.rket.shared.core.domain.command.DomainCommand;

public record MembershipRemove(Long tenantId)
        implements DomainCommand {
}
