package tech.rket.shared.core.domain.command;

import tech.rket.shared.core.domain.result.DomainConstraintViolation;

import java.util.List;

public interface DomainCommand {

    default List<DomainConstraintViolation> validate() {
        return DomainCommandValidator.validate(this);
    }
}
