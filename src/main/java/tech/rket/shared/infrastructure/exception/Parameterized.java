package tech.rket.shared.infrastructure.exception;

import java.util.Collection;

public interface Parameterized {
    <T extends Collection<Object>> T getParameters();
}
