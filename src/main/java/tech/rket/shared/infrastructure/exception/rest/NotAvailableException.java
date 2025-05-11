package tech.rket.shared.infrastructure.exception.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Alimodares
 * @since 2020-12-15
 */
@ResponseStatus(HttpStatus.GONE)
public class NotAvailableException extends RuntimeException {
    public NotAvailableException(String message) {
        super(message);
    }
}
