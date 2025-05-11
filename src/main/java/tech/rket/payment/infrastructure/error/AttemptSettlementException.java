package tech.rket.payment.infrastructure.error;

public class AttemptSettlementException extends RuntimeException {
    public AttemptSettlementException(String message) {
        super(message);
    }

    public AttemptSettlementException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
