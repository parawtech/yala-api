package tech.rket.auth.presentation.rest.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.workspace.info.WorkspaceInfo;

import java.util.List;

@RestController
@RequestMapping("/auth/admin/tenant/{tenantId}/workspace")
@RequiredArgsConstructor
@Tag(name = "Tenant Workspace Administration", description = "Manage workspaces")
public class WorkspaceAdminController {

    @Operation(summary = "Update workspace by TenantId and identifier", description = "Update information of a workspace by tenantId and identifier.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Workspace updated successfully", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Workspace not found")})
    @PutMapping("/{identifier}")
    public ResponseEntity<Void> update(@Parameter(description = "ID of the tenant", required = true) @PathVariable("tenantId") Long tenantId, @Parameter(description = "Identifier of the workspace", required = true) @PathVariable("identifier") String identifier) {
        throw new NotImplementedException();
    }

    @Operation(summary = "Update workspace by TenantId and identifier", description = "Update information of a workspace by tenantId and identifier.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Workspace updated successfully", content = @Content(mediaType = "application/json")), @ApiResponse(responseCode = "404", description = "Workspace not found")})
    @PutMapping
    public ResponseEntity<Void> update(@Parameter(description = "ID of the tenant", required = true) @PathVariable("tenantId") Long tenantId) {
        throw new NotImplementedException();
    }

    @Operation(summary = "Get workspace by TenantId and Identifier", description = "Fetch details of a workspace by tenantId and identifier.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Workspace retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceInfo.class))), @ApiResponse(responseCode = "404", description = "Workspace not found", content = @Content)})
    @GetMapping("/{identifier}")
    public ResponseEntity<WorkspaceInfo> get(@Parameter(description = "ID of the tenant", required = true) @PathVariable("tenantId") Long tenantId, @Parameter(description = "Identifier of the workspace", required = true) @PathVariable("identifier") String identifier) {
        throw new NotImplementedException();
    }

    @Operation(summary = "Get list of workspaces by TenantId", description = "Fetch details of all workspaces of a tenant by tenantId.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Workspaces retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = WorkspaceInfo.class))), @ApiResponse(responseCode = "404", description = "Workspaces not found", content = @Content)})
    @GetMapping
    public ResponseEntity<Page<WorkspaceInfo>> getAll(@Parameter(description = "ID of the tenant", required = true) @PathVariable("tenantId") Long tenantId, Pageable pageable) {

        throw new NotImplementedException();
    }


    @Operation(summary = "Delete all workspaces by TenantId", description = "Deletes all workspaces for a given tenantId.")
    @ApiResponse(responseCode = "202", description = "Workspaces deleted successfully", content = @Content)
    @DeleteMapping
    public ResponseEntity<Void> deleteAll(@PathVariable("tenantId") Long tenantId) {
        throw new NotImplementedException();
    }

    @Operation(summary = "Delete workspace by TenantId and Identifier", description = "Deletes a specific workspace by tenantId and identifier.")
    @ApiResponse(responseCode = "202", description = "Workspace deleted successfully", content = @Content)
    @DeleteMapping("/{identifier}")
    public ResponseEntity<Void> delete(@PathVariable("tenantId") Long tenantId, @PathVariable("identifier") String identifier) {
        throw new NotImplementedException();
    }
}
