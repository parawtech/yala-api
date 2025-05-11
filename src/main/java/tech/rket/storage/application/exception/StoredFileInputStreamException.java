package tech.rket.storage.application.exception;

import tech.rket.shared.infrastructure.exception.Problem;
import lombok.Getter;

import java.io.IOException;

@Problem(code = "storage.storedFile.inputStreamIoException", status = "400")
@Getter
public class StoredFileInputStreamException extends RuntimeException {
    public StoredFileInputStreamException(IOException e) {
        super("Input stream is not valid.", e);
    }
}
