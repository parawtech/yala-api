package tech.rket.payment.infrastructure.error;

import tech.rket.shared.infrastructure.exception.rest.NotFoundException;

public class TransactionDoesNotExistException extends NotFoundException {
    public TransactionDoesNotExistException(String ownerIdentifier, String reference) {
        super(String.format("Transaction %s for %s does not exist.", reference, ownerIdentifier));
    }
}
