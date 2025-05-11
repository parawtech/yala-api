package tech.rket.auth.application.tenant.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(status = "NOT_FOUND")
public class TenantDoesNotExistsException extends RuntimeException {
    public TenantDoesNotExistsException(Object value) {
    }
}
