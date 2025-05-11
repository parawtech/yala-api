package tech.rket.auth.infrastructure.persistence.tenant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

import java.util.Set;

@Entity
@Table(name = "auth_tenant")
@NoArgsConstructor
@Getter
public class TenantEntity extends BaseEntity implements PersistedObject<Long> {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;
    private String workDomain;
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private Set<RoleEntity> roles;

    public static TenantEntity build(Long id, String workDomain, String name, Set<RoleEntity> roles) {
        TenantEntity tenantEntity = new TenantEntity();
        tenantEntity.id = id;
        tenantEntity.workDomain = workDomain;
        tenantEntity.name = name;
        tenantEntity.roles = roles;
        return tenantEntity;
    }

    public void update(String name) {
        this.name = name;
    }
}
