package tech.rket.payment.domain.transaction;

public enum TransactionStatus {
    INITIATED,
    CANCELED,
    INVALID,
    ATTEMPTED,
    PAID,
    FAILED;

    public boolean isFinal() {
        return this == TransactionStatus.FAILED || this == TransactionStatus.PAID || this == TransactionStatus.INVALID || this == TransactionStatus.CANCELED;
    }
}
