package tech.rket.auth.application.tenant.info;

import tech.rket.auth.application.user.info.UserInfo;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;

import java.time.Instant;

public record TenantInvitationInfo(
        UserInfo inviter,
        UserInfo invitee,
        RoleInfo role,
        InvitationStatus status,
        Instant expiredAt,
        Instant createdAt,
        Instant updatedAt,
        boolean isRegistered
) {
}
