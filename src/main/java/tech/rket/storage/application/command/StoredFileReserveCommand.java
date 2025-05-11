package tech.rket.storage.application.command;

import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuthType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StoredFileReserveCommand(@NotNull String characteristic,
                                       @NotNull String key,
                                       @NotNull StoredFileAuthType authType,
                                       @Nullable List<String> authValues,
                                       @NotNull MimeType mimeType) {
    @Override
    public String toString() {
        return String.format("%s,%s", characteristic, key);
    }
}
