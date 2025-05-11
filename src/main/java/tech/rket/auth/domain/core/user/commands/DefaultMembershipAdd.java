package tech.rket.auth.domain.core.user.commands;

import tech.rket.shared.core.domain.command.DomainCommand;
import tech.rket.auth.domain.core.tenant.Tenant;

public record DefaultMembershipAdd(Tenant tenant, String roleId)
        implements DomainCommand {
}
