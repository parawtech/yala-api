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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.user.UserManagementService;
import tech.rket.auth.application.user.UserQueryService;
import tech.rket.auth.application.user.command.AdminUpdatePasswordCommand;
import tech.rket.auth.application.user.command.UserUpdateProfileCommand;
import tech.rket.auth.application.user.info.UserInfo;
import tech.rket.auth.domain.core.user.entity.Membership;
import tech.rket.auth.domain.query.user.UserLite;

import java.util.Set;

@RestController
@RequestMapping("auth/admin/user")
@RequiredArgsConstructor
@PreAuthorize("isAdmin()")
@Tag(name = "User Administration", description = "Manage user profiles, passwords, memberships, and invitations.")
public class UserAdminController {
    private final UserQueryService userQueryService;
    private final UserManagementService userManagementService;

    @Operation(summary = "Update user password", description = "Allows an admin to update a user's password.")
    @ApiResponse(responseCode = "202", description = "Password updated successfully", content = @Content)
    @PutMapping("{userId}/password")
    public ResponseEntity<Void> updatePassword(@Parameter(description = "ID of the user whose password will be updated", required = true)
                                               @PathVariable(value = "userId") Long userId,
                                               @Valid @RequestBody AdminUpdatePasswordCommand command) {
        userManagementService.updatePassword(userId, command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Update user profile", description = "Allows an admin to update a user's profile.")
    @ApiResponse(responseCode = "202", description = "Profile updated successfully", content = @Content)
    @PutMapping("{userId}")
    public ResponseEntity<Void> updateProfile(@Parameter(description = "ID of the user whose profile will be updated", required = true)
                                              @PathVariable(value = "userId") Long userId,
                                              @Valid @RequestBody UserUpdateProfileCommand command) {
        userManagementService.updateProfile(userId, command);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Delete user", description = "Allows an admin to delete a user by their ID.")
    @ApiResponse(responseCode = "202", description = "User deleted successfully", content = @Content)
    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to delete", required = true)
                                           @PathVariable("userId") Long userId) {
        userManagementService.deleteUser(userId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Get list of users", description = "Fetch a paginated list of users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserLite.class)))
    @GetMapping
    public ResponseEntity<Page<UserLite>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userQueryService.getUsers(pageable));
    }

    @Operation(summary = "Get user by ID", description = "Fetch details of a specific user by their ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfo.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("{userId}")
    public ResponseEntity<UserInfo> getUser(@Parameter(description = "ID of the user", required = true)
                                            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userQueryService.getUser(userId));
    }

    @Operation(summary = "Get user memberships", description = "Fetch the memberships (tenants) of a specific user.")
    @ApiResponse(responseCode = "200", description = "Memberships retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Membership.class)))
    @GetMapping("{userId}/membership")
    public ResponseEntity<Set<Membership>> getMemberships(@Parameter(description = "ID of the user", required = true)
                                                          @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userQueryService.getMemberships(userId));
    }

    @Operation(summary = "Remove user from membership", description = "Allows an admin to remove a user from a specific tenant.")
    @ApiResponse(responseCode = "202", description = "User removed from membership", content = @Content)
    @DeleteMapping("{userId}/membership/{tenantId}")
    public ResponseEntity<Void> leftMembership(@Parameter(description = "ID of the user", required = true)
                                               @PathVariable("userId") Long userId,
                                               @Parameter(description = "ID of the tenant to leave", required = true)
                                               @PathVariable("tenantId") Long tenantId) {
        userManagementService.leftMembership(userId, tenantId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Get user invitations", description = "Fetch the pending invitations of a specific user.")
    @ApiResponse(responseCode = "200", description = "Invitations retrieved successfully", content = @Content)
    @GetMapping("{userId}/invite")
    public ResponseEntity<Object> getInvites(@Parameter(description = "ID of the user", required = true)
                                             @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userQueryService.getInvites(userId));
    }
}
