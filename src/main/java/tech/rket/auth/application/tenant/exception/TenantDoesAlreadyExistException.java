package tech.rket.auth.application.tenant.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(status = "CONFLICT")
public class TenantDoesAlreadyExistException extends RuntimeException {
    public TenantDoesAlreadyExistException(Object id) {
    }
}
