package tech.rket.storage.domain.command;

import tech.rket.shared.core.domain.command.DomainCommand;
import tech.rket.storage.domain.StoredFile;
import jakarta.validation.constraints.NotNull;

public record StoredFileParentAssign(@NotNull StoredFile parent, @NotNull String key)
        implements DomainCommand {
}
