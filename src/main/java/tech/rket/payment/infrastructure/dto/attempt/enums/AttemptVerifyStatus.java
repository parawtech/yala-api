package tech.rket.payment.infrastructure.dto.attempt.enums;

import tech.rket.payment.domain.attempt.AttemptStatus;

import java.util.Optional;

public enum AttemptVerifyStatus {
    PAID,
    FAILED,
    VERIFYING;

    public Optional<AttemptStatus> convertToAttemptStatus() {
        AttemptStatus attemptStatus = switch (this) {
            case PAID -> AttemptStatus.PAID;
            case FAILED -> AttemptStatus.FAILED;
            default -> null;
        };
        return Optional.ofNullable(attemptStatus);
    }

    public boolean isFinal() {
        return this == AttemptVerifyStatus.PAID || this == AttemptVerifyStatus.FAILED;
    }
}
