package tech.rket.auth.domain.core.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class UserJoinInvitation {
    private User inviter;
    private Tenant tenant;
    private String role;
    @Setter
    private InvitationStatus status;
    private Instant expiredAt;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isExpired() {
        return expiredAt != null && expiredAt.isBefore(Instant.now());
    }
}
