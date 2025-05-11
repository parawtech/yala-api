package tech.rket.storage.application.command;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record StoredFileContentPatchCommand(@NotNull Long id,
                                            @NotNull MultipartFile file) {
}