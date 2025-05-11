package tech.rket.auth.domain.core.user.commands;

import tech.rket.shared.core.domain.command.DomainCommand;
import tech.rket.auth.domain.core.tenant.Tenant;

public record MembershipAdd(Tenant tenant, String role)
        implements DomainCommand {
}
