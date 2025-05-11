package tech.rket.storage.domain.command;

import tech.rket.storage.domain.StoredFile;

public record StoredFileContentDelete(StoredFile file) {
}
