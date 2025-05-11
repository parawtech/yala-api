package tech.rket.storage.application.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(code = "storedFile.mimeType.doesNotSupported", details = "rejectedMimeType", status = "BAD_REQUEST")
public class StoredFileNotSupportedException extends RuntimeException {
    public StoredFileNotSupportedException(String contentType) {
    }
}
