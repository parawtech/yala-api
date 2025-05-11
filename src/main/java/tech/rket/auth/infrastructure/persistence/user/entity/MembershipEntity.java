package tech.rket.auth.infrastructure.persistence.user.entity;

import tech.rket.auth.infrastructure.persistence.tenant.entity.RoleEntity;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_user_membership")
@NoArgsConstructor
@Getter
public class MembershipEntity extends BaseEntity {
    @Id
    @JID(group = 0, type = 5)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private RoleEntity role;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    private boolean isDefault;
    @OneToMany(mappedBy = "membership", cascade = CascadeType.ALL)
    private Set<SessionEntity> sessions;

    public static MembershipEntity build(UserEntity user, RoleEntity role, boolean isDefault) {
        MembershipEntity membership = new MembershipEntity();
        membership.user = user;
        membership.role = role;
        membership.isDefault = isDefault;
        membership.sessions = new HashSet<>();
        return membership;
    }
}
