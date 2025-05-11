package tech.rket.payment.infrastructure.error;

public class AttemptVerifyException extends RuntimeException {
    public AttemptVerifyException(String message) {
        super(message);
    }

    public AttemptVerifyException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
