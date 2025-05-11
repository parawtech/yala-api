package tech.rket.auth.infrastructure.persistence.tenant.entity;

import tech.rket.auth.infrastructure.persistence.permission.entity.PermissionEntity;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auth_tenant_role_permission")
@EqualsAndHashCode(callSuper = true, exclude = {"role"})
@NoArgsConstructor
@Getter
public class RolePermissionEntity extends BaseEntity {
    @Id
    @JID(type = 3, group = 0)
    private Long id;
    @ManyToOne
    private RoleEntity role;
    @ManyToOne
    private PermissionEntity permission;

    public static RolePermissionEntity build(Long id, RoleEntity role, PermissionEntity permission) {
        RolePermissionEntity rpe = new RolePermissionEntity();
        rpe.id = id;
        rpe.role = role;
        rpe.permission = permission;
        return rpe;
    }
}
