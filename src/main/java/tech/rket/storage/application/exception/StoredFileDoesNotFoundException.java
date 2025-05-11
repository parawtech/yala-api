package tech.rket.storage.application.exception;

import com.ibm.icu.text.MessageFormat;
import tech.rket.shared.infrastructure.exception.Parameterized;
import tech.rket.shared.infrastructure.exception.Problem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Problem(code = "storage.storedFile.doesNotFound", status = "404")
@Getter
public class StoredFileDoesNotFoundException extends RuntimeException implements Parameterized {
    private final List<Object> parameters = new ArrayList<>();

    public StoredFileDoesNotFoundException(Object id) {
        super(MessageFormat.format("Stored file {0} is not found.", id.toString()));
        parameters.add(id.toString());
    }
}
