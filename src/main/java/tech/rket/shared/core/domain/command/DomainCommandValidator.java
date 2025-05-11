package tech.rket.shared.core.domain.command;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;

class DomainCommandValidator {
    private static final ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .buildValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static List<DomainConstraintViolation> validate(DomainCommand command) {
        List<DomainConstraintViolation> constraintViolations = new ArrayList<>();
        var violations = validator.validate(command);
        if (violations != null && !violations.isEmpty()) {
            constraintViolations.addAll(violations.stream().map(CommandValidatorConstraintViolation::new).toList());
        }
        return constraintViolations;
    }
}
