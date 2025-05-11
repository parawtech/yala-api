package tech.rket.storage.domain.command;

import tech.rket.shared.core.domain.command.DomainCommand;
import jakarta.validation.constraints.NotNull;

public record StoredFileMetadataAdd(@NotNull String key, @NotNull Object value) implements DomainCommand {
}
