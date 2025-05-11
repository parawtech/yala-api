package tech.rket.auth.domain.core.user.entity;

import tech.rket.auth.domain.core.tenant.Tenant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tech.rket.auth.domain.core.user.value_object.InvitationStatus;
import tech.rket.auth.domain.core.user.User;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Invitation {
    private User inviter;
    private Tenant tenant;
    private String role;
    @Setter(AccessLevel.PACKAGE)
    private InvitationStatus status;
    private Instant expiredAt;
    private Integer version;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isExpired() {
        return expiredAt != null && expiredAt.isBefore(Instant.now());
    }
}
