package tech.rket.storage.domain.command;

import tech.rket.shared.core.domain.command.DomainCommand;
import tech.rket.storage.domain.value_object.MimeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StoredFileSendForUpload(@NotNull @Min(1) Long size, @NotNull MimeType mimeType) implements DomainCommand {
}
