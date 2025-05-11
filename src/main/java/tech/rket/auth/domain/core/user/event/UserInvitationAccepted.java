package tech.rket.auth.domain.core.user.event;

import tech.rket.auth.domain.core.user.entity.Invitation;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.core.user.User;

public record UserInvitationAccepted(
        User user,
        Invitation invitation,
        Membership membership
) {
}
