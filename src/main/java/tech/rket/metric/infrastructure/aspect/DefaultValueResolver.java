package tech.rket.metric.infrastructure.aspect;

import io.micrometer.common.annotation.ValueResolver;

public class DefaultValueResolver implements ValueResolver {
    @Override
    public String resolve(Object target) {
        return (String) target;
    }
}