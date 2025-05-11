package tech.rket.auth.presentation.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.permission.PermissionManagementService;
import tech.rket.auth.application.permission.PermissionQueryService;
import tech.rket.auth.application.permission.command.PermissionCreateCommand;
import tech.rket.auth.application.permission.command.PermissionUpdateCommand;
import tech.rket.auth.domain.core.permission.Permission;
import tech.rket.auth.domain.query.permission.PermissionLite;

@RestController
@RequestMapping("/auth/admin/permission")
@RequiredArgsConstructor
@Tag(name = "Permission Administration", description = "Manage permissions for users, roles, and resources.")
public class PermissionAdminController {
    private final PermissionQueryService queryService;
    private final PermissionManagementService managementService;

    @Operation(summary = "Create a new permission", description = "Creates a new permission in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Permission created successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid permission data", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> addPermission(@RequestBody PermissionCreateCommand command) {
        managementService.create(command);
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "Get all permissions", description = "Fetches a paginated list of all permissions.")
    @ApiResponse(responseCode = "200", description = "List of permissions retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermissionLite.class)))
    @GetMapping
    public ResponseEntity<Page<PermissionLite>> getAllPermissions(Pageable pageable) {
        return ResponseEntity.ok(queryService.getAll(pageable));
    }

    @Operation(summary = "Get a specific permission", description = "Fetches details of a specific permission by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permission retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Permission.class))),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @GetMapping("{permissionIdentifier}")
    public ResponseEntity<Permission> getPermission(
            @Parameter(description = "Identifier of the permission", required = true)
            @PathVariable("permissionIdentifier") String identifier) {
        return ResponseEntity.ok(queryService.get(identifier));
    }

    @Operation(summary = "Delete a permission", description = "Deletes a specific permission by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Permission deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @DeleteMapping("{permissionIdentifier}")
    public ResponseEntity<Void> deletePermission(
            @Parameter(description = "Identifier of the permission to delete", required = true)
            @PathVariable("permissionIdentifier") String identifier) {
        managementService.delete(identifier);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update a permission", description = "Updates the details of an existing permission.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Permission updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Permission not found", content = @Content)
    })
    @PutMapping("{permissionIdentifier}")
    public ResponseEntity<Void> updatePermission(
            @Parameter(description = "Identifier of the permission to update", required = true)
            @PathVariable("permissionIdentifier") String identifier,
            @RequestBody PermissionUpdateCommand request) {
        managementService.update(identifier, request);
        return ResponseEntity.accepted().build();
    }
}
