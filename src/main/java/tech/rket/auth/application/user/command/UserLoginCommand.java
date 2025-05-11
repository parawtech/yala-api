package tech.rket.auth.application.user.command;

import tech.rket.auth.application.user.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserLoginCommand(Long tenant,
                               @Email String email,
                               @NotNull @Password String password) {
}
