package tech.rket.auth.domain.core.permission;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tech.rket.auth.domain.core.permission.command.PermissionCreate;
import tech.rket.auth.domain.core.permission.command.PermissionUpdate;
import tech.rket.auth.domain.core.permission.event.PermissionCreated;
import tech.rket.auth.domain.core.permission.event.PermissionUpdated;
import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.domain.SharedAggregateRoot;
import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import tech.rket.shared.core.domain.result.DomainResult;
import tech.rket.shared.core.query.QueryObject;

import java.time.Instant;
import java.util.List;


@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@AllArgsConstructor
public class Permission extends SharedAggregateRoot<String>
        implements DomainObject.Entity.AggregateRoot<String>, QueryObject<String> {
    private String id;
    private String name;
    private String description;
    private Instant createdDate;
    private Instant updatedDate;
    private Integer version;

    public static DomainResult<Permission> create(PermissionCreate create) {
        List<DomainConstraintViolation> violations = create.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        Permission permission = new Permission(create.id(), create.name(), create.description(), null, null, null);
        permission.registerEvent(new PermissionCreated(
                create.id(), Instant.now(), create.name(), create.description()
        ));
        return DomainResult.success(permission);
    }

    public DomainResult<PermissionUpdated> update(PermissionUpdate command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        this.name = command.name();
        this.description = command.description();
        var event = new PermissionUpdated(this.getId(), Instant.now(), name, description);
        registerEvent(event);
        return DomainResult.success(event);
    }
}
