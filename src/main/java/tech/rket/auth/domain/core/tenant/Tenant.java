package tech.rket.auth.domain.core.tenant;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.core.tenant.command.*;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.tenant.entity.TenantJoinInvitation;
import tech.rket.auth.domain.core.tenant.entity.TenantRegisterInvitation;
import tech.rket.auth.domain.core.tenant.event.*;
import tech.rket.auth.domain.core.service.AuthInvitationPredicate;
import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.domain.SharedAggregateRoot;
import tech.rket.shared.core.domain.result.DomainResult;
import tech.rket.shared.core.query.QueryObject;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static tech.rket.auth.domain.core.tenant.TenantConstraintViolation.*;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
@AllArgsConstructor
public class Tenant extends SharedAggregateRoot<Long>
        implements DomainObject.Entity.AggregateRoot<Long>, QueryObject<Long> {
    private final Long id;
    private String name;
    private String workDomain;
    private Instant createdDate;
    private Instant updatedDate;
    private Instant deletedDate;
    private final Set<Role> roles = new LinkedHashSet<>();
    private final Integer version;
    private final Set<TenantJoinInvitation> joinInvitations = new LinkedHashSet<>();
    private final Set<TenantRegisterInvitation> registerInvitations = new LinkedHashSet<>();

    public static DomainResult<Tenant> create(TenantCreate create) {
        Tenant tenant = new Tenant(create.id(), create.name(), create.workDomain(),
                null, null, null, null);
        tenant.registerEvent(new TenantCreated(tenant.getId(), Instant.now()));
        return DomainResult.success(tenant);
    }

    public DomainResult<TenantDisabled> disable() {
        this.deletedDate = Instant.now();
        var event = new TenantDisabled(this.getId(), Instant.now());
        registerEvent(event);
        return DomainResult.success(event);
    }

    public DomainResult<TenantUpdated> update(TenantUpdate update) {
        this.name = update.name();
        var event = new TenantUpdated(this.getId(), Instant.now(), name);
        registerEvent(event);
        return DomainResult.success(event);
    }

    public DomainResult<RoleUpdated> update(String id, RoleUpdate update) {
        return getRole(id)
                .map(r -> r.update(update))
                .map(r -> r.map(role -> new RoleUpdated(this.getId(), Instant.now(), role)))
                .orElseGet(() -> DomainResult.fail(TenantConstraintViolation.ROLE_DOES_NOT_FOUND))
                .peek(this::registerEvent);
    }

    public DomainResult<RoleCreated> add(RoleCreate roleCreate) {
        return Role.create(roleCreate.id(), roleCreate.name(), roleCreate.description())
                .peek(roles::add)
                .map(role -> new RoleCreated(this.getId(), Instant.now(), role))
                .peek(this::registerEvent);
    }

    public DomainResult<RoleDeleted> remove(String roleId) {
        return getRole(roleId).stream()
                .peek(roles::remove).findFirst()
                .map(role -> new RoleDeleted(this.getId(), Instant.now(), role))
                .map(DomainResult::success)
                .orElseGet(() -> DomainResult.fail(TenantConstraintViolation.ROLE_DOES_NOT_FOUND))
                .peek(this::registerEvent);
    }

    public DomainResult<RolePermissionAdded> add(String role, Permission permission) {
        return getRole(role)
                .map(r -> r.add(permission))
                .orElseGet(() -> DomainResult.fail(TenantConstraintViolation.ROLE_DOES_NOT_FOUND))
                .map(r -> new RolePermissionAdded(this.getId(), Instant.now(), r))
                .peek(this::registerEvent);
    }

    public DomainResult<RolePermissionRemoved> remove(String role, Permission permission) {
        return getRole(role)
                .map(r -> r.remove(permission))
                .map(r -> r.map(v -> new RolePermissionRemoved(this.getId(), Instant.now(), v)))
                .orElseGet(() -> DomainResult.fail(TenantConstraintViolation.ROLE_DOES_NOT_FOUND))
                .peek(this::registerEvent);

    }

    public Optional<Role> getRole(String id) {
        return roles.stream()
                .filter(r -> id.equals(r.getId()))
                .findAny();
    }

    public DomainResult<TenantRegisterInvited> inviteRegister(AuthInvitationPredicate predicate, TenantRegisterInvite command) {
        if (findLastRegisterInvitation(command.auth()).isPresent()) {
            return DomainResult.fail(REGISTRATION_INVITATION_DOES_EXISTS_ALREADY);
        }
        if (!predicate.canBeInvited(this, command.role(), command.auth())) {
            return DomainResult.fail(USER_CAN_NOT_BE_INVITED);
        }
        Optional<Role> roleOptional = getRole(command.role());
        if (roleOptional.isEmpty()) {
            return DomainResult.fail(ROLE_DOES_NOT_FOUND);
        }
        Role role = roleOptional.get();
        TenantRegisterInvitation invitation = new TenantRegisterInvitation(
                command.inviter(),
                role,
                command.auth(),
                command.expiredAt(), null, null);
        registerInvitations.add(invitation);
        var event = new TenantRegisterInvited(this.getId(), Instant.now(), invitation);
        registerEvent(event);
        return DomainResult.success(event);
    }

    public DomainResult<TenantRegisterInvitationRevoked> revokeRegisterInvitation(TenantRegisterInvitationRevoke command) {
        Optional<TenantRegisterInvitation> lastInvitation = findLastRegisterInvitation(command.auth());
        if (lastInvitation.isEmpty()) {
            return DomainResult.fail(REGISTRATION_INVITATION_DOES_NOT_FOUND);
        }
        registerInvitations.remove(lastInvitation.get());
        var event = new TenantRegisterInvitationRevoked(this.getId(), Instant.now(), lastInvitation.get());
        registerEvent(event);
        return DomainResult.success(event);
    }

    public Optional<TenantRegisterInvitation> findLastRegisterInvitation(String auth) {
        return registerInvitations.stream()
                .filter(s -> s.auth().equalsIgnoreCase(auth))
                .max(Comparator.comparing(TenantRegisterInvitation::expiredAt));
    }

    public boolean isWorkTenant() {
        return workDomain != null;
    }
}
