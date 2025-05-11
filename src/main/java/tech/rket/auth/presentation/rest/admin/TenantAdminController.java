package tech.rket.auth.presentation.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.tenant.TenantManagementService;
import tech.rket.auth.application.tenant.TenantQueryService;
import tech.rket.auth.application.tenant.command.RoleCreateCommand;
import tech.rket.auth.application.tenant.command.RoleUpdateCommand;
import tech.rket.auth.application.tenant.command.TenantInviteUserCommand;
import tech.rket.auth.application.tenant.command.TenantUpdateCommand;
import tech.rket.auth.application.tenant.info.TenantInfo;
import tech.rket.auth.domain.core.tenant.entity.Role;
import tech.rket.auth.domain.query.tenant.TenantLite;

import java.util.Set;

@RestController
@RequestMapping("/auth/admin/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant Administration", description = "Manage tenants, roles, and invitations for users in the tenant system.")
public class TenantAdminController {
    private final TenantManagementService managementService;
    private final TenantQueryService queryService;

    @Operation(summary = "Get tenants", description = "Fetch a paginated list of tenants.")
    @ApiResponse(responseCode = "200", description = "List of tenants retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TenantLite.class)))
    @GetMapping
    public ResponseEntity<Page<TenantLite>> getTenants(Pageable pageable) {
        return ResponseEntity.ok(queryService.getAll(pageable));
    }

    @Operation(summary = "Get tenant by ID", description = "Fetch details of a specific tenant by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tenant retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TenantInfo.class))),
            @ApiResponse(responseCode = "404", description = "Tenant not found", content = @Content)
    })
    @GetMapping("{tenantId}")
    public ResponseEntity<TenantInfo> getTenant(@Parameter(description = "ID of the tenant", required = true)
                                                @PathVariable("tenantId") Long id) {
        return ResponseEntity.ok(queryService.get(id));
    }

    @Operation(summary = "Delete tenant", description = "Deletes a tenant by its ID.")
    @ApiResponse(responseCode = "202", description = "Tenant deleted successfully", content = @Content)
    @DeleteMapping("{tenantId}")
    public ResponseEntity<Void> deleteTenant(@Parameter(description = "ID of the tenant to delete", required = true)
                                             @PathVariable("tenantId") Long id) {
        managementService.delete(id);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update tenant", description = "Updates a tenant's details.")
    @ApiResponse(responseCode = "202", description = "Tenant updated successfully", content = @Content)
    @PutMapping("{tenantId}")
    public ResponseEntity<Void> updateTenant(@Parameter(description = "ID of the tenant to update", required = true)
                                             @PathVariable("tenantId") Long id,
                                             @Valid @RequestBody TenantUpdateCommand command) {
        managementService.update(id, command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Add role to tenant", description = "Adds a new role to a specific tenant.")
    @ApiResponse(responseCode = "201", description = "Role added successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class)))
    @PostMapping("{tenantId}/role")
    public ResponseEntity<Role> addRole(@Parameter(description = "ID of the tenant to add role", required = true)
                                        @PathVariable("tenantId") Long id,
                                        @Valid @RequestBody RoleCreateCommand command) {
        Role role = managementService.addRole(id, command);
        return ResponseEntity.status(201).body(role);
    }

    @Operation(summary = "Get roles for tenant", description = "Fetches a list of roles assigned to a specific tenant.")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class)))
    @GetMapping("{tenantId}/role")
    public ResponseEntity<Set<Role>> getRoles(@Parameter(description = "ID of the tenant", required = true)
                                              @PathVariable("tenantId") Long id) {
        return ResponseEntity.status(200).body(queryService.getRoles(id));
    }

    @Operation(summary = "Delete role from tenant", description = "Deletes a specific role from a tenant.")
    @ApiResponse(responseCode = "202", description = "Role deleted successfully", content = @Content)
    @DeleteMapping("{tenantId}/role/{roleId}")
    public ResponseEntity<Void> deleteRole(@Parameter(description = "ID of the tenant", required = true)
                                           @PathVariable("tenantId") Long id,
                                           @Parameter(description = "ID of the role to delete", required = true)
                                           @PathVariable("roleId") String roleId) {
        managementService.deleteRole(id, roleId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update role in tenant", description = "Updates an existing role in a specific tenant.")
    @ApiResponse(responseCode = "202", description = "Role updated successfully", content = @Content)
    @PutMapping("{tenantId}/role/{roleId}")
    public ResponseEntity<Void> updateRole(@Parameter(description = "ID of the tenant", required = true)
                                           @PathVariable("tenantId") Long identifier,
                                           @Parameter(description = "ID of the role to update", required = true)
                                           @PathVariable("roleId") String roleId,
                                           @Valid @RequestBody RoleUpdateCommand request) {
        managementService.updateRole(identifier, roleId, request);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Invite user to tenant", description = "Sends an invitation to a user to join a tenant.")
    @ApiResponse(responseCode = "202", description = "User invited successfully", content = @Content)
    @PatchMapping("{tenantId}/invite")
    public ResponseEntity<Void> inviteUser(@Parameter(description = "ID of the tenant", required = true)
                                           @PathVariable("tenantId") Long identifier,
                                           @RequestBody @Valid TenantInviteUserCommand command) {
        managementService.invite(identifier, command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Revoke user invitation", description = "Revokes an invitation sent to a user to join a tenant.")
    @ApiResponse(responseCode = "202", description = "Invitation revoked successfully", content = @Content)
    @DeleteMapping("{tenantId}/invite")
    public ResponseEntity<Void> inviteUser(@Parameter(description = "ID of the tenant", required = true)
                                           @PathVariable("tenantId") Long identifier,
                                           @Parameter(description = "Auth identifier of the user", required = true)
                                           @RequestParam("auth") String auth) {
        managementService.revokeInvitation(identifier, auth);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Add permission to role", description = "Adds a permission to a role in a specific tenant.")
    @ApiResponse(responseCode = "202", description = "Permission added to role", content = @Content)
    @PatchMapping("{tenantId}/role/{roleId}/permission/{permissionIdentifier}")
    public ResponseEntity<Void> addRolePermission(@Parameter(description = "ID of the tenant", required = true)
                                                  @PathVariable("tenantId") Long identifier,
                                                  @Parameter(description = "ID of the role", required = true)
                                                  @PathVariable("roleId") String roleId,
                                                  @Parameter(description = "Identifier of the permission", required = true)
                                                  @PathVariable("permissionIdentifier") String permissionIdentifier) {
        managementService.addRolePermission(identifier, roleId, permissionIdentifier);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Remove permission from role", description = "Removes a permission from a role in a specific tenant.")
    @ApiResponse(responseCode = "202", description = "Permission removed from role", content = @Content)
    @DeleteMapping("{tenantId}/role/{roleId}/permission/{permissionIdentifier}")
    public ResponseEntity<Void> deleteRolePermission(@Parameter(description = "ID of the tenant", required = true)
                                                     @PathVariable("tenantId") Long identifier,
                                                     @Parameter(description = "ID of the role", required = true)
                                                     @PathVariable("roleId") String roleId,
                                                     @Parameter(description = "Identifier of the permission", required = true)
                                                     @PathVariable("permissionIdentifier") String permissionIdentifier) {
        managementService.deleteRolePermission(identifier, roleId, permissionIdentifier);
        return ResponseEntity.accepted().build();
    }
}
