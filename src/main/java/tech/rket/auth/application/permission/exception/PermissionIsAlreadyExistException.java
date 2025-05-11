package tech.rket.auth.application.permission.exception;

import lombok.Getter;
import tech.rket.shared.infrastructure.exception.Parameterized;
import tech.rket.shared.infrastructure.exception.Problem;

import java.util.ArrayList;
import java.util.List;

@Problem(code = "auth.permission.alreadyExists", status = "CONFLICT")
@Getter
public class PermissionIsAlreadyExistException extends RuntimeException implements Parameterized {
    private final List<Object> parameters = new ArrayList<>();

    public PermissionIsAlreadyExistException(String identifier) {
        super("Permission " + identifier + " does already exist.");
        parameters.add(identifier);
    }
}
