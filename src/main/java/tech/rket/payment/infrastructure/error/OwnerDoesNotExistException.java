package tech.rket.payment.infrastructure.error;

import tech.rket.shared.infrastructure.exception.rest.NotFoundException;

public class OwnerDoesNotExistException extends NotFoundException {
    public OwnerDoesNotExistException(String ownerIdentifier) {
        super(String.format("Owner %s does not exist.", ownerIdentifier));
    }
}
