package tech.rket.auth.application.user.command;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import tech.rket.auth.application.user.validation.Password;

public record UserRegisterCommand(
        String mobile,
        String email,
        @NotNull @Password String password,
        @Nullable Long invitedTenantId,
        @Nullable String locale
) {
    public boolean hasAuth() {
        return !((mobile() == null || mobile().isEmpty()) &&
                (email() == null || email().isEmpty()));
    }
}
