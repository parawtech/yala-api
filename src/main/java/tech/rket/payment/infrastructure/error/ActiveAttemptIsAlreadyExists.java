package tech.rket.payment.infrastructure.error;

public class ActiveAttemptIsAlreadyExists extends RuntimeException {
    public ActiveAttemptIsAlreadyExists(String reference) {
    }
}
