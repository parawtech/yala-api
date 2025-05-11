package tech.rket.auth.presentation.rest.panel;

import tech.rket.auth.application.tenant.TenantManagementService;
import tech.rket.auth.application.tenant.info.TenantInvitationListInfo;
import tech.rket.auth.application.tenant.TenantQueryService;
import tech.rket.auth.application.tenant.command.RoleCreateCommand;
import tech.rket.auth.application.tenant.command.RoleUpdateCommand;
import tech.rket.auth.application.tenant.command.TenantInviteUserCommand;
import tech.rket.auth.application.tenant.command.TenantUpdateCommand;
import tech.rket.auth.application.tenant.info.TenantInfo;
import tech.rket.auth.application.user.UserManagementService;
import tech.rket.auth.domain.core.tenant.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/auth/panel/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant Panel", description = "Manage tenant details, roles, and invitations for the authenticated tenant user.")
public class TenantPanelController {
    private final TenantQueryService queryService;
    private final TenantManagementService managementService;
    private final UserManagementService userService;

    @Operation(summary = "Get tenant information", description = "Fetches details of the current tenant associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Tenant information retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TenantInfo.class)))
    @GetMapping
    public ResponseEntity<TenantInfo> getTenant() {
        return ResponseEntity.ok(queryService.getCurrentTenant());
    }

    @Operation(summary = "Delete tenant", description = "Deletes the current tenant and logs out the user.")
    @ApiResponse(responseCode = "202", description = "Tenant deleted successfully", content = @Content)
    @DeleteMapping
    public ResponseEntity<Void> deleteTenant() {
        managementService.deleteCurrentTenant();
        userService.logout();
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update tenant information", description = "Updates the current tenant's information.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Tenant updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid update information", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Void> updateTenant(@Valid @RequestBody TenantUpdateCommand command) {
        managementService.updateCurrentTenant(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Create a role", description = "Adds a new role to the current tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "400", description = "Invalid role creation data", content = @Content)
    })
    @PostMapping("role")
    public ResponseEntity<Role> addRole(@Valid @RequestBody RoleCreateCommand command) {
        Role role = managementService.addCurrentTenantRole(command);
        return ResponseEntity.status(201).body(role);
    }

    @Operation(summary = "Get roles", description = "Lists all roles associated with the current tenant.")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Role.class)))
    @GetMapping("role")
    public ResponseEntity<Set<Role>> getRoles() {
        return ResponseEntity.status(200).body(queryService.getCurrentTenantRoles());
    }

    @Operation(summary = "Delete a role", description = "Removes a role from the current tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Role deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @DeleteMapping("role/{roleId}")
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "ID of the role to delete", required = true)
            @PathVariable("roleId") String roleId) {
        managementService.deleteCurrentTenantRole(roleId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update a role", description = "Updates details of a specified role in the current tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Role updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
    })
    @PutMapping("role/{roleId}")
    public ResponseEntity<Void> updateRole(
            @Parameter(description = "ID of the role to update", required = true)
            @PathVariable("roleId") String roleId,
            @Valid @RequestBody RoleUpdateCommand request) {
        managementService.updateCurrentTenantRole(roleId, request);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Get tenant invitations", description = "Retrieves all invitations sent for the current tenant.")
    @ApiResponse(responseCode = "200", description = "Tenant invitations retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TenantInvitationListInfo.class)))
    @GetMapping("invite")
    public ResponseEntity<TenantInvitationListInfo> getInvites() {
        return ResponseEntity.ok(queryService.getInvites());
    }

    @Operation(summary = "Invite a user", description = "Sends an invitation to a user to join the current tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Invitation sent successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid invitation details", content = @Content)
    })
    @PostMapping("invite")
    public ResponseEntity<Void> inviteUser(@RequestBody @Valid TenantInviteUserCommand command) {
        managementService.createCurrentTenantInvitation(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Revoke an invitation", description = "Revokes an invitation sent to a user to join the current tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Invitation revoked successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Invitation not found", content = @Content)
    })
    @DeleteMapping("invite")
    public ResponseEntity<Void> revokeInvitation(
            @Parameter(description = "Email or identifier of the invitee", required = true)
            @RequestParam("auth") String auth) {
        managementService.revokeCurrentTenantInvitation(auth);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Add permission to a role", description = "Adds a specific permission to a specified role within the tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Permission added successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role or permission not found", content = @Content)
    })
    @PatchMapping("role/{roleId}/permission/{permissionIdentifier}")
    public ResponseEntity<Void> addRolePermission(
            @Parameter(description = "ID of the role", required = true)
            @PathVariable("roleId") String roleId,
            @Parameter(description = "Identifier of the permission to add", required = true)
            @PathVariable("permissionIdentifier") String permissionIdentifier) {
        managementService.addCurrentTenantRolePermission(roleId, permissionIdentifier);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Remove permission from a role", description = "Removes a specific permission from a specified role within the tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Permission removed successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Role or permission not found", content = @Content)
    })
    @DeleteMapping("role/{roleId}/permission/{permissionIdentifier}")
    public ResponseEntity<Void> deleteRolePermission(
            @Parameter(description = "ID of the role", required = true)
            @PathVariable("roleId") String roleId,
            @Parameter(description = "Identifier of the permission to remove", required = true)
            @PathVariable("permissionIdentifier") String permissionIdentifier) {
        managementService.deleteCurrentTenantRolePermission(roleId, permissionIdentifier);
        return ResponseEntity.accepted().build();
    }
}
