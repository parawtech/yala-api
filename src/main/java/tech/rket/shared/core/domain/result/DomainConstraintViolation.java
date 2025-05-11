package tech.rket.shared.core.domain.result;

import java.util.Collection;
import java.util.Map;

public interface DomainConstraintViolation {
    String code();

    String message();

    interface Parameterized extends DomainConstraintViolation {
        <T extends Collection<Object>> T getParameters();
    }

    interface NamedParameterized extends DomainConstraintViolation {
        Map<String, Object> getParameters();
    }
}
