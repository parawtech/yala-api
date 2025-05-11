package tech.rket.auth.application.user.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(status = "401")
public class UserIsNotAuthenticatedException extends RuntimeException {
    public UserIsNotAuthenticatedException(Object value) {
    }
}
