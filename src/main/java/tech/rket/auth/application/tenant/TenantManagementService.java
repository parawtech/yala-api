package tech.rket.auth.application.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tech.rket.auth.application.permission.exception.PermissionDoesNotExistException;
import tech.rket.auth.application.tenant.command.RoleCreateCommand;
import tech.rket.auth.application.tenant.command.RoleUpdateCommand;
import tech.rket.auth.application.tenant.command.TenantInviteUserCommand;
import tech.rket.auth.application.tenant.command.TenantUpdateCommand;
import tech.rket.auth.application.tenant.exception.RoleDoesNotFoundException;
import tech.rket.auth.application.tenant.exception.TenantDoesNotExistsException;
import tech.rket.auth.application.user.AuthInvitationPredicateImpl;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.core.permission.PermissionRepository;
import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.tenant.TenantFillRepository;
import tech.rket.auth.domain.core.tenant.TenantRepository;
import tech.rket.auth.domain.core.tenant.command.*;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.core.tenant.entity.RolePermission;
import tech.rket.auth.domain.core.user.User;
import tech.rket.auth.domain.core.user.UserFillRepository;
import tech.rket.auth.domain.core.user.UserRepository;
import tech.rket.auth.domain.core.user.commands.UserJoinInvitationRevoke;
import tech.rket.auth.domain.core.user.commands.UserJoinInvite;
import tech.rket.shared.infrastructure.auth.UserLoginInfo;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TenantManagementService {
    private final TenantRepository repository;
    private final TenantFillRepository tenantFillRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AuthInvitationPredicateImpl authInvitationPredicate;
    private final UserFillRepository userFillRepository;

    public void delete(Long id) {
        Tenant tenant = repository.findById(id).orElseThrow(() -> new TenantDoesNotExistsException(id));
        tenant.disable();
        repository.save(tenant);
    }

    public void update(Long id, TenantUpdateCommand tenantUpdateParam) {
        Tenant tenant = repository.findById(id).orElseThrow(() -> new TenantDoesNotExistsException(id));
        tenant.update(new TenantUpdate(tenantUpdateParam.name()))
                .throwIfFailure(DomainConstraintViolationException::new);
        repository.save(tenant);
    }

    @Transactional
    public void invite(Long id, TenantInviteUserCommand command) {
        Tenant tenant = repository.findById(id).orElseThrow(() -> new TenantDoesNotExistsException(id));
        User inviter = userRepository.findById(UserLoginInfo.getCurrent().userId()).orElseThrow();

        Optional<User> userOptional = userRepository.findByEmail(command.auth());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.inviteJoin(
                            authInvitationPredicate,
                            new UserJoinInvite(inviter, tenant, command.role(), command.expiredAt()))
                    .throwIfFailure(DomainConstraintViolationException::new);
            userRepository.save(user);
        } else {
            tenant.inviteRegister(
                            authInvitationPredicate,
                            new TenantRegisterInvite(
                                    inviter,
                                    command.role(),
                                    command.auth(),
                                    Instant.now().plus(1, ChronoUnit.DAYS)
                            ))
                    .throwIfFailure(DomainConstraintViolationException::new);
            repository.save(tenant);
        }
    }

    public Role addRole(Long identifier, RoleCreateCommand command) {
        Tenant tenant = repository.findById(identifier).orElseThrow(() -> new TenantDoesNotExistsException(identifier));
        RoleCreate roleCreate = new RoleCreate(command.identifier(), command.name(), command.description());
        Role role = tenant.add(roleCreate).throwIfFailure(DomainConstraintViolationException::new).value().role();
        repository.save(tenant);
        return tenant.getRole(role.getId())
                .orElseThrow(() -> new RoleDoesNotFoundException(role.getId()));
    }

    public void deleteRole(Long identifier, String roleId) {
        Tenant tenant = repository.findById(identifier).orElseThrow(() -> new TenantDoesNotExistsException(identifier));
        Role deleted = tenant.remove(roleId).throwIfFailure(DomainConstraintViolationException::new).value().role();
        repository.delete(tenant, deleted.getId());
    }

    public void updateRole(Long identifier, String roleId, RoleUpdateCommand command) {
        Tenant tenant = repository.findById(identifier).orElseThrow(() -> new TenantDoesNotExistsException(identifier));
        tenant.update(roleId, new RoleUpdate(command.identifier(), command.name(), command.description()));
        repository.save(tenant);
    }

    public void addRolePermission(Long identifier, String roleId, String permissionIdentifier) {
        Tenant tenant = repository.findById(identifier).orElseThrow(() -> new TenantDoesNotExistsException(identifier));
        Permission permission = permissionRepository.findById(permissionIdentifier).orElseThrow(() -> new PermissionDoesNotExistException(permissionIdentifier));
        tenant.add(roleId, permission).throwIfFailure(DomainConstraintViolationException::new);
        repository.save(tenant);
    }

    public void deleteRolePermission(Long identifier, String roleId, String permissionIdentifier) {
        Tenant tenant = repository.findById(identifier).orElseThrow(() -> new TenantDoesNotExistsException(identifier));
        Permission permission = permissionRepository.findById(permissionIdentifier).orElseThrow(() -> new PermissionDoesNotExistException(permissionIdentifier));
        Role role = tenant.getRole(roleId).orElseThrow(() -> new RoleDoesNotFoundException(roleId));
        RolePermission rolePermission = tenant.remove(roleId, permission).throwIfFailure(DomainConstraintViolationException::new).value().permission();
        repository.delete(tenant, role.getId(), rolePermission.id());
    }

    public void deleteCurrentTenant() {
        delete(UserLoginInfo.getCurrent().tenantId());
    }

    public void updateCurrentTenant(TenantUpdateCommand command) {
        update(UserLoginInfo.getCurrent().tenantId(), command);
    }

    public Role addCurrentTenantRole(RoleCreateCommand command) {
        return addRole(UserLoginInfo.getCurrent().tenantId(), command);
    }

    public void deleteCurrentTenantRole(String roleId) {
        deleteRole(UserLoginInfo.getCurrent().tenantId(), roleId);
    }

    public void updateCurrentTenantRole(String roleId, RoleUpdateCommand request) {
        updateRole(UserLoginInfo.getCurrent().tenantId(), roleId, request);
    }

    @Transactional
    public void createCurrentTenantInvitation(TenantInviteUserCommand command) {
        invite(UserLoginInfo.getCurrent().tenantId(), command);
    }

    @Transactional
    public void revokeCurrentTenantInvitation(String auth) {
        revokeInvitation(UserLoginInfo.getCurrent().tenantId(), auth);
    }

    @Transactional
    public void addCurrentTenantRolePermission(String roleId, String permissionIdentifier) {
        addRolePermission(UserLoginInfo.getCurrent().tenantId(), roleId, permissionIdentifier);
    }

    @Transactional
    public void deleteCurrentTenantRolePermission(String roleId, String permissionIdentifier) {
        deleteRolePermission(UserLoginInfo.getCurrent().tenantId(), roleId, permissionIdentifier);
    }

    @Transactional
    public void revokeInvitation(Long tenantId, String auth) {
        Tenant tenant = repository.findById(tenantId).orElseThrow();
        Optional<User> user = userRepository.findByEmail(auth);
        if (user.isPresent()) {
            userFillRepository.fillJoinInvitations(user.get());
            tenantFillRepository.fillJoinInvitations(tenant);
            user.get().revokeJoinInvitation(new UserJoinInvitationRevoke(tenantId))
                    .throwIfFailure(DomainConstraintViolationException::new);
            userRepository.save(user.get());
        } else {
            tenantFillRepository.fillRegisterInvitations(tenant);
            tenant.revokeRegisterInvitation(new TenantRegisterInvitationRevoke(auth))
                    .throwIfFailure(DomainConstraintViolationException::new);
            repository.deleteRegisterInvitation(tenantId, auth);
        }
    }
}
