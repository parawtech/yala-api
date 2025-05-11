package tech.rket.auth.application.user.info;

import tech.rket.auth.application.tenant.info.RoleInfo;
import tech.rket.auth.application.tenant.info.TenantInfo;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;

import java.time.Instant;


public record InvitationInfo(
        UserInfo inviter,
        TenantInfo tenant,
        RoleInfo role,
        InvitationStatus status,
        Instant expiredAt,
        Instant createdAt,
        Instant updatedAt
) {
}
