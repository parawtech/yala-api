package tech.rket.auth.infrastructure.persistence.user.entity;

import tech.rket.auth.domain.core.user.value_object.InvitationStatus;
import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "auth_user_invite")
@NoArgsConstructor
@Getter
public class InvitationEntity extends BaseEntity {
    @Id
    @JID(group = 0, type = 7)
    private Long id;
    @ManyToOne
    private RoleEntity role;
    @ManyToOne
    private UserEntity user;
    @ManyToOne
    private UserEntity inviter;
    @Setter
    @Enumerated(EnumType.STRING)
    private InvitationStatus status;
    private Instant expiredAt;

    public static InvitationEntity build(UserEntity user, UserEntity inviter, RoleEntity role, InvitationStatus status, Instant expiredAt) {
        InvitationEntity invitation = new InvitationEntity();
        invitation.user = user;
        invitation.inviter = inviter;
        invitation.role = role;
        invitation.expiredAt = expiredAt;
        invitation.status = status;
        return invitation;
    }
}
