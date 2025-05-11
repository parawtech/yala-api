package tech.rket.auth.domain.core.tenant;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;

public enum TenantConstraintViolation implements DomainConstraintViolation {
    ROLE_DOES_NOT_FOUND("Role does not found."),
    ROLE_PERMISSION_DOES_NOT_FOUND("Permission in role does not found."),
    ROLE_PERMISSION_DOES_EXISTS_ALREADY("Permission in role does exists already."),
    REGISTRATION_INVITATION_DOES_EXISTS_ALREADY("Registration invitation does exists already."),
    REGISTRATION_INVITATION_DOES_NOT_FOUND("Registration invitation does not found."),
    USER_CAN_NOT_BE_INVITED("User can not be invited.");
    private final String message;

    TenantConstraintViolation(String s) {
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
