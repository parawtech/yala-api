package tech.rket.auth.application.tenant.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(status = "BAD_REQUEST")
public class RoleDoesNotFoundException extends RuntimeException {
    public RoleDoesNotFoundException(Object value) {
    }
}
