package tech.rket.auth.application.tenant.exception;

import tech.rket.shared.infrastructure.exception.Problem;

@Problem(status = "400", code = "auth.user.isNotInvitedInThisTenantAlready")
public class UserNotInvitedInThisTenantAlready extends RuntimeException {
    public UserNotInvitedInThisTenantAlready(String auth) {
    }
}
