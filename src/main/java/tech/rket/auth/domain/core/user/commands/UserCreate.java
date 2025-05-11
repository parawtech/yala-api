package tech.rket.auth.domain.core.user.commands;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import tech.rket.shared.core.domain.command.DomainCommand;

public record UserCreate(@NotNull Long id,
                         @NotNull String locale,
                         @Email String email,
                         String mobile)
        implements DomainCommand {
}
