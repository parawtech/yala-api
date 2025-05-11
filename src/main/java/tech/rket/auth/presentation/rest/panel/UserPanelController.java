package tech.rket.auth.presentation.rest.panel;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.user.UserManagementService;
import tech.rket.auth.application.user.UserQueryService;
import tech.rket.auth.application.user.command.UserUpdatePasswordCommand;
import tech.rket.auth.application.user.command.UserUpdateProfileCommand;
import tech.rket.auth.application.user.info.UserInfo;
import tech.rket.auth.domain.core.user.entity.Membership;

import java.util.Set;

@RestController
@RequestMapping("/auth/panel/user")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Panel", description = "Manage user profile, password, memberships, and invitations for the authenticated user.")
public class UserPanelController {
    private final UserQueryService userQueryService;
    private final UserManagementService userService;

    @Operation(summary = "Update user password", description = "Allows the authenticated user to update their password. Requires the current password for confirmation.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Password updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid password format or incorrect current password", content = @Content)
    })
    @PutMapping("password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody UserUpdatePasswordCommand command) {
        userService.updateCurrentUserPassword(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Retrieve user profile", description = "Fetches the profile information of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfo.class)))
    @GetMapping
    public ResponseEntity<UserInfo> getUser() {
        return ResponseEntity.ok(userQueryService.getCurrentUser());
    }

    @Operation(summary = "Update user profile", description = "Allows the authenticated user to update their profile information.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "User profile updated successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid profile data", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody UserUpdateProfileCommand command) {
        userService.updateCurrentProfile(command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Delete user account", description = "Deletes the authenticated user's account.")
    @ApiResponse(responseCode = "202", description = "User account deleted successfully", content = @Content)
    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Retrieve user memberships", description = "Lists all tenant memberships associated with the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User memberships retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Membership.class)))
    @GetMapping("membership")
    public ResponseEntity<Set<Membership>> getMemberships() {
        return ResponseEntity.ok(userQueryService.getCurrentUserMemberships());
    }

    @Operation(summary = "Leave tenant membership", description = "Allows the authenticated user to leave a specific tenant.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Membership left successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Tenant or membership not found", content = @Content)
    })
    @DeleteMapping("membership/{tenantId}")
    public ResponseEntity<Void> leftMembership(
            @Parameter(description = "ID of the tenant to leave", required = true)
            @PathVariable("tenantId") Long tenantId) {
        userService.leftCurrentUserMembership(tenantId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Retrieve user invitations", description = "Lists all tenant invitations for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "User invitations retrieved successfully",
            content = @Content(mediaType = "application/json"))
    @GetMapping("invite")
    public ResponseEntity<Object> getInvites() {
        return ResponseEntity.ok(userQueryService.getCurrentUserInvites());
    }

    @Operation(summary = "Reject tenant invitation", description = "Allows the authenticated user to reject a specific tenant invitation.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Invitation rejected successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Invitation not found", content = @Content)
    })
    @DeleteMapping("invite/{tenantId}")
    public ResponseEntity<Void> rejectInvitation(
            @Parameter(description = "ID of the tenant invitation to reject", required = true)
            @PathVariable("tenantId") Long tenantId) {
        userService.rejectCurrentUserInvitation(tenantId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Accept tenant invitation", description = "Allows the authenticated user to accept a specific tenant invitation.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Invitation accepted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Invitation not found", content = @Content)
    })
    @PatchMapping("invite/{tenantId}")
    public ResponseEntity<Void> acceptInvitation(
            @Parameter(description = "ID of the tenant invitation to accept", required = true)
            @PathVariable("tenantId") Long tenantId) {
        userService.acceptCurrentUserInvitation(tenantId);
        return ResponseEntity.accepted().build();
    }
}
