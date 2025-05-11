package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class IllegalStateResolver implements RestExceptionResolver<IllegalStateException> {
    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull IllegalStateException ex, @Nonnull WebRequest request) {
        return RestErrorMessage.builder()
                .code("illegalState")
                .namedArguments(false)
                .status(500)
                .build();
    }

    @Override
    public Class<IllegalStateException> supportedClass() {
        return IllegalStateException.class;
    }
}
