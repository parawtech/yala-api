package tech.rket.auth.application.user.command;


import tech.rket.auth.application.user.validation.Password;
import jakarta.validation.constraints.NotNull;

public record UserUpdatePasswordCommand(@NotNull @Password String current,
                                        @NotNull @Password String newPassword) {
}
