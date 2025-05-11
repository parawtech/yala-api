package tech.rket.storage.application.command;

import tech.rket.storage.domain.value_object.StoredFileAuthType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record StoredFileUploadCommand(@NotNull String characteristic,
                                      @NotNull String key,
                                      @NotNull StoredFileAuthType authType,
                                      @Nullable List<String> authValues,
                                      @NotNull MultipartFile file) {
    @Override
    public String toString() {
        return String.format("%s,%s", characteristic, key);
    }
}
