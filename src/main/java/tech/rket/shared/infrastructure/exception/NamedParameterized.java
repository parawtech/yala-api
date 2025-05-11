package tech.rket.shared.infrastructure.exception;

import java.util.Map;

public interface NamedParameterized {
    Map<String, Object> getParameters();
}
