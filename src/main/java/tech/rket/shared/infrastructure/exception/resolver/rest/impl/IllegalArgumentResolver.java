package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class IllegalArgumentResolver implements RestExceptionResolver<IllegalArgumentException> {
    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull IllegalArgumentException ex, @Nonnull WebRequest request) {
        return RestErrorMessage.builder()
                .code("illegalArgument")
                .namedArguments(false)
                .status(400)
                .build();
    }

    @Override
    public Class<IllegalArgumentException> supportedClass() {
        return IllegalArgumentException.class;
    }
}
