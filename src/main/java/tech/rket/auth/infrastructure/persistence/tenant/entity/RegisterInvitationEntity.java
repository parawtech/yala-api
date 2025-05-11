package tech.rket.auth.infrastructure.persistence.tenant.entity;

import tech.rket.auth.infrastructure.persistence.user.entity.UserEntity;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "auth_register_invitation")
@NoArgsConstructor
@Getter
public class RegisterInvitationEntity extends BaseEntity {
    @Id
    @JID(group = 0, type = 8)
    private Long id;
    private String auth;
    @ManyToOne(fetch = FetchType.EAGER)
    private RoleEntity role;
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity inviter;
    private Instant expiredAt;

    public static RegisterInvitationEntity create(String auth, RoleEntity role, UserEntity inviter, Instant expiredAt) {
        RegisterInvitationEntity entity = new RegisterInvitationEntity();
        entity.auth = auth;
        entity.role = role;
        entity.inviter = inviter;
        entity.expiredAt = expiredAt;
        return entity;
    }
}
