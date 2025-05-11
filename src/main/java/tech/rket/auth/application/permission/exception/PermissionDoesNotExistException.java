package tech.rket.auth.application.permission.exception;

import lombok.Getter;
import tech.rket.shared.infrastructure.exception.Problem;

import java.util.ArrayList;
import java.util.List;

@Problem(code = "auth.permission.doesNotExists", status = "NOT_FOUND")
@Getter
public class PermissionDoesNotExistException extends RuntimeException {
    private final List<Object> parameters = new ArrayList<>();

    public PermissionDoesNotExistException(String identifier) {
        super("Permission " + identifier + " does not exist.");
        parameters.add(identifier);
    }
}
