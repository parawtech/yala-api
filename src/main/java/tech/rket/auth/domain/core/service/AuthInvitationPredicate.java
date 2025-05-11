package tech.rket.auth.domain.core.service;

import tech.rket.auth.domain.core.tenant.Tenant;

public interface AuthInvitationPredicate {
    boolean canBeInvited(Tenant tenant, String role, String auth);
}
