package tech.rket.shared.core.domain.command;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class CommandValidatorConstraintViolation implements DomainConstraintViolation, DomainConstraintViolation.NamedParameterized {
    @Getter
    private final Map<String, Object> parameters = new HashMap<>();
    private final String code;
    private final String message;

    CommandValidatorConstraintViolation(ConstraintViolation<?> violation) {
        this.code = violation.getMessageTemplate().replace("{", "").replace("}", "");
        this.message = violation.getMessage();
        parameters.put("value", violation.getInvalidValue());
        parameters.put("path", violation.getPropertyPath().toString());
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
