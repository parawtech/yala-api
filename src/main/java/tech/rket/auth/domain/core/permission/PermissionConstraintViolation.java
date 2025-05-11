package tech.rket.auth.domain.core.permission;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;

public enum PermissionConstraintViolation implements DomainConstraintViolation {
    ;
    private final String message;

    PermissionConstraintViolation(String s) {
        message = s;
    }

    @Override
    public String code() {
        return name();
    }

    @Override
    public String message() {
        return message;
    }
}
