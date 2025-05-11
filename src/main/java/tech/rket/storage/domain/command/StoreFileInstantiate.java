package tech.rket.storage.domain.command;

import tech.rket.shared.core.domain.command.DomainCommand;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuth;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record StoreFileInstantiate(
        @NotNull Long id,
        @NotNull Long tenant,
        @NotNull Long user,
        @NotNull String characteristic,
        @NotNull String key,
        @Nullable Long size,
        @Nullable MimeType mimeType,
        @NotNull StoredFileAuth auth,
        @Nullable StoredFile parent,
        @Nullable String variantName
) implements DomainCommand {
}
