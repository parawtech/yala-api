package tech.rket.auth.application.tenant.info;

import tech.rket.auth.domain.core.tenant.entity.TenantJoinInvitation;
import tech.rket.auth.domain.core.tenant.entity.TenantRegisterInvitation;

import java.util.Set;

public record TenantInvitationListInfo(
        Set<TenantRegisterInvitation> registerInvitations,
        Set<TenantJoinInvitation> joinInvitations
) {
}
