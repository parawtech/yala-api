package tech.rket.auth.domain.core.tenant.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.core.tenant.command.RoleUpdate;
import tech.rket.shared.core.domain.result.DomainResult;

import java.time.Instant;
import java.util.*;

import static tech.rket.auth.domain.core.tenant.TenantConstraintViolation.ROLE_PERMISSION_DOES_EXISTS_ALREADY;
import static tech.rket.auth.domain.core.tenant.TenantConstraintViolation.ROLE_PERMISSION_DOES_NOT_FOUND;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Role {
    private String id;
    private String name;
    private String description;
    private Instant createdDate;
    private Instant updatedDate;
    private Integer version;

    private Set<RolePermission> permissions = new HashSet<>();

    public Set<RolePermission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public static DomainResult<Role> create(String identifier, String name, String description) {
        Role role = new Role(identifier,
                name,
                description,
                null, null, null, new LinkedHashSet<>());
        return DomainResult.success(role);
    }

    public DomainResult<RolePermission> add(Permission permission) {
        return getPermission(permission.getId())
                .map(r -> DomainResult.<RolePermission>fail(ROLE_PERMISSION_DOES_EXISTS_ALREADY))
                .orElseGet(() -> {
                    RolePermission rp = new RolePermission(permission.getId(), permission.getName());
                    permissions.add(rp);
                    return DomainResult.success(rp);
                });
    }

    public DomainResult<RolePermission> remove(Permission permission) {
        return getPermission(permission.getId()).stream()
                .peek(permissions::remove).findFirst()
                .map(DomainResult::success)
                .orElseGet(() -> DomainResult.fail(ROLE_PERMISSION_DOES_NOT_FOUND));
    }

    public DomainResult<Role> update(RoleUpdate update) {
        this.id = update.identifier() == null ? this.id : update.identifier();
        this.name = update.name() == null ? this.name : update.name();
        this.description = update.description() == null ? this.description : update.description();
        return DomainResult.success(this);
    }

    public Optional<RolePermission> getPermission(String permissionId) {
        return permissions.stream().filter(rp -> rp.id().equals(permissionId)).findFirst();
    }
}
