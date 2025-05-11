package tech.rket.auth.application.user.exception;

import lombok.Getter;
import tech.rket.shared.infrastructure.exception.Parameterized;
import tech.rket.shared.infrastructure.exception.Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Problem(code = "auth.session.doesNotFound", status = "NOT_FOUND")
@Getter
public class SessionDoesNotFoundException extends RuntimeException implements Parameterized {
    private final List<Object> parameters = new ArrayList<>();

    public SessionDoesNotFoundException(UUID sessionId) {
        super("Session with id " + sessionId + " does not exist");
        parameters.add(sessionId.toString());
    }
}
