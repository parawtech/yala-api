package tech.rket.storage.application.exception;

import com.ibm.icu.text.MessageFormat;
import tech.rket.shared.infrastructure.exception.Parameterized;
import tech.rket.shared.infrastructure.exception.Problem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Problem(code = "storage.storedFile.doesAlreadyExists", status = "409")
@Getter
public class StoredFileDoesAlreadyExistsException extends RuntimeException implements Parameterized {
    private final List<Object> parameters = new ArrayList<>();

    public StoredFileDoesAlreadyExistsException(Object file) {
        super(MessageFormat.format("Stored file {0} does exist already.", file));
        parameters.add(file.toString());
    }
}
