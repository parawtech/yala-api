package tech.rket.auth.domain.core.user;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;

public enum UserConstraintViolation implements DomainConstraintViolation {
    MEMBERSHIP_DOES_NOT_FOUND("Membership does not found."),
    MEMBERSHIP_DOES_EXIST_ALREADY("Membership does exists already."),
    SESSION_DOES_NOT_FOUND("Session does not found."),
    INVITE_PENDING_DOES_EXISTS_ALREADY("Another Invitation for this tenant is already exists."),
    INVITE_IS_REJECTED_ALREADY("Invitation is rejected already."),
    INVITE_IS_ACCEPTED_ALREADY("Invitation is accepted already."),
    INVITE_IS_REVOKED_ALREADY("Invitation is accepted already."),
    INVITE_IS_EXPIRED_ALREADY("Invitation is expired already."),
    USER_CAN_NOT_BE_INVITED("User cannot be invited.");
    private final String message;

    UserConstraintViolation(String s) {
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
