package tech.rket.storage.domain.command;

import tech.rket.storage.domain.StoredFile;

import java.io.InputStream;

public record StoredFileContentUpload(StoredFile file, InputStream stream) {
}
