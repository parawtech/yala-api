package tech.rket.storage.domain.command;

import tech.rket.shared.core.domain.command.DomainCommand;
import jakarta.validation.constraints.NotNull;

public record StoredFileMetadataDelete(@NotNull String key) implements DomainCommand {
}
