package tech.rket.payment.infrastructure.dto.attempt.enums;

import tech.rket.payment.domain.attempt.AttemptStatus;

import java.util.Optional;

public enum AttemptCallbackStatus {
    VERIFIABLE, PAID, FAILED;

    public Optional<AttemptStatus> convertToAttemptStatus() {
        AttemptStatus attemptStatus = switch (this) {
            case PAID -> AttemptStatus.PAID;
            case FAILED -> AttemptStatus.FAILED;
            default -> null;
        };
        return Optional.ofNullable(attemptStatus);
    }

    public boolean isFinal() {
        return this == AttemptCallbackStatus.FAILED || this == AttemptCallbackStatus.PAID;
    }
}
