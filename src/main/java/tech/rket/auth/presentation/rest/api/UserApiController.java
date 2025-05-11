package tech.rket.auth.presentation.rest.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.rket.auth.application.user.UserManagementService;
import tech.rket.auth.application.user.command.UserLoginCommand;
import tech.rket.auth.application.user.command.UserRegisterCommand;
import tech.rket.auth.application.user.info.OAuth;
import tech.rket.auth.application.user.info.UserInfo;

import java.util.UUID;

@RestController
@RequestMapping("/auth/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "This API handles user registration, login, authentication token management, and account verification. Provides endpoints for user creation, login, existence check by email, logout, and token refresh.")
public class UserApiController {
    private final UserManagementService userService;

    @Operation(
            summary = "Send otp to email and/or mobile",
            description = "This endpoint send otp to user by their email address and/or mobile number."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Otp send", content = @Content),
    })

    @PostMapping("otp")
    @PreAuthorize("permitAll()")
    public ResponseEntity<OtpResult> sendOtp(
            @Parameter(description = "Email address of the user to send otp", required = true)
            @RequestParam(value = "email", required = false) @Valid @Email String email,
            @Parameter(description = "Mobile number of the user to send otp", required = true)
            @RequestParam(value = "mobile", required = false) @Valid String mobile
    ) {
        OtpResult value = userService.sendOtp(mobile, email);
        return ResponseEntity.accepted().body(value);
    }

    @Operation(
            summary = "Check if a user exists",
            description = "This endpoint checks if a user exists by their email address and/or mobile number without requiring an authentication token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User exists", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @RequestMapping(method = RequestMethod.HEAD)
    @PreAuthorize("permitAll()")
    public ResponseEntity<Void> existenceUser(
            @Parameter(description = "Email address of the user to check for existence", required = true)
            @RequestParam(value = "email", required = false) @Valid @Email String email,
            @Parameter(description = "Mobile number of the user to check for existence", required = true)
            @RequestParam(value = "mobile", required = false) @Valid String mobile
    ) {
        boolean exists = userService.existsByAuth(mobile, email);
        return exists ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Register a new user and associate with a tenant",
            description = """
                    Registers a new user in the system.
                    - If `invitedTenantId` is null or the email is a free email, a personal tenant is created for the user.
                    - If `invitedTenantId` is not null and the email is a work email, the tenant handling depends on whether a tenant with the email's domain already exists:
                      * **New Domain**: A new 'work' tenant is created and associated with this domain.
                      * **Existing Domain**: a personal tenant is created for the user.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserInfo.class)))
    })
    @PostMapping
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserInfo> register(
            @Parameter(description = "User registration data, including tenant invite ID (if applicable)", required = true)
            @Valid @RequestBody UserRegisterCommand command
    ) {
        UserInfo userInfo = userService.create(command);
        return ResponseEntity.status(201).body(userInfo);
    }

    @Operation(
            summary = "User login",
            description = "Logs in a user and returns an OAuth token scoped to the specified tenant. If no tenant is specified, logs in to the default tenant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OAuth.class)))
    })
    @PostMapping(value = "login", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<OAuth> login(
            @Parameter(description = "User login data", required = true)
            @Valid @RequestBody UserLoginCommand command
    ) {
        OAuth token = userService.login(command);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "User logout",
            description = "Logs out the currently authenticated user by invalidating their token and refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Logout successful", content = @Content)
    })
    @DeleteMapping(value = "logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OAuth> logout() {
        userService.logout();
        return ResponseEntity.accepted().build();
    }

    @Operation(
            summary = "Refresh user token",
            description = "Refreshes the user's OAuth token if the request includes a valid refresh token. " +
                    "Note that after a token is refreshed, it will no longer be valid."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OAuth.class)))
    })
    @PatchMapping(value = "refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isRefreshToken()")
    public ResponseEntity<OAuth> refresh() {
        UUID tokenId = (UUID) SecurityContextHolder.getContext().getAuthentication().getDetails();
        OAuth token = userService.refresh(tokenId);
        return ResponseEntity.ok(token);
    }
}
