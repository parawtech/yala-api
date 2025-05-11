package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class ConstraintViolationResolver implements RestExceptionResolver<ConstraintViolationException> {
    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull ConstraintViolationException ex, @Nonnull WebRequest request) {
        RestErrorMessage restErrorMessage = RestErrorMessage.builder()
                .code("constraintViolation")
                .status(400)
                .build();
        Stream.of(ex.getMessage().split(","))
                .forEach(it -> {
                    String[] error = it.split(": ");
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", error[0]);
                    map.put("message", error[1]);
                    restErrorMessage.getDetails().add(map);
                });
        return restErrorMessage;
    }

    @Override
    public Class<ConstraintViolationException> supportedClass() {
        return ConstraintViolationException.class;
    }
}
