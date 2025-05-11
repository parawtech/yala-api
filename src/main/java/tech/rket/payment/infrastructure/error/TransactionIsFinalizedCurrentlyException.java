package tech.rket.payment.infrastructure.error;

import tech.rket.shared.infrastructure.exception.rest.BadRequestException;

public class TransactionIsFinalizedCurrentlyException extends BadRequestException {
    public TransactionIsFinalizedCurrentlyException() {
        super("Transaction is finalized currently");
    }
}
