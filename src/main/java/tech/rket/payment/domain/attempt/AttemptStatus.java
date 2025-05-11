package tech.rket.payment.domain.attempt;

import tech.rket.payment.domain.transaction.TransactionStatus;

import java.util.Optional;

public enum AttemptStatus {
    INITIATED,
    REDIRECTED,
    PAID,
    FAILED,
    INVALID,
    VERIFYING;

    public Optional<TransactionStatus> convertToTransactionStatus() {
        TransactionStatus transactionStatus = switch (this) {
            case PAID -> TransactionStatus.PAID;
            case FAILED -> TransactionStatus.FAILED;
            case INVALID -> TransactionStatus.INVALID;
            default -> null;
        };
        return Optional.ofNullable(transactionStatus);
    }

    public boolean isFinal() {
        return this == PAID || this == FAILED || this == INVALID;
    }
}
