package tech.rket.payment.infrastructure.dto.attempt.enums;

import tech.rket.payment.domain.attempt.AttemptStatus;

import java.util.Optional;

public enum AttemptSettlementStatus {
    PAID,
    FAILED,
    INVALID,
    REDIRECTABLE,
    TEMPORARY_ERROR;

    public Optional<AttemptStatus> convertToAttemptStatus() {
        AttemptStatus attemptStatus = switch (this) {
            case PAID -> AttemptStatus.PAID;
            case FAILED -> AttemptStatus.FAILED;
            case INVALID -> AttemptStatus.INVALID;
            default -> null;
        };
        return Optional.ofNullable(attemptStatus);
    }

    public boolean isFinal() {
        return this == AttemptSettlementStatus.FAILED || this == AttemptSettlementStatus.PAID ||
                this == AttemptSettlementStatus.INVALID;
    }

}
