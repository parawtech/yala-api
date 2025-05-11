package tech.rket.auth.infrastructure.persistence.tenant.entity;

import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "auth_tenant_role")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Getter
public class RoleEntity extends BaseEntity {
    @Id
    @JID(type = 2, group = 0)
    private Long id;
    @Column(nullable = false)
    private String identifier;
    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToOne(cascade = CascadeType.ALL)
    private TenantEntity tenant;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private Set<RolePermissionEntity> permissions;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RegisterInvitationEntity> registerInvitations;

    public static RoleEntity build(Long id, String identifier, String name, String description, TenantEntity tenant, Set<RolePermissionEntity> permissions) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.id = id;
        roleEntity.identifier = identifier;
        roleEntity.name = name;
        roleEntity.description = description;
        roleEntity.tenant = tenant;
        roleEntity.permissions = permissions;
        roleEntity.registerInvitations = new LinkedHashSet<>();
        return roleEntity;
    }

    public void update(String identifier, String name, String description) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;

    }
}