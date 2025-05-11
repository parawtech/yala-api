package tech.rket.payment.infrastructure.error;

public class AttemptCallbackException extends RuntimeException {
    public AttemptCallbackException(String message) {
        super(message);
    }

    public AttemptCallbackException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
