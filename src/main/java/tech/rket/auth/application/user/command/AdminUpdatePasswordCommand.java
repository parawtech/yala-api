package tech.rket.auth.application.user.command;


import tech.rket.auth.application.user.validation.Password;
import jakarta.validation.constraints.NotNull;

public record AdminUpdatePasswordCommand(@NotNull @Password String newPassword) {
}
