package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.exception.rest.CustomException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class CustomExceptionResolver implements RestExceptionResolver<CustomException> {

    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull CustomException ex, @Nonnull WebRequest request) {
        return RestErrorMessage.builder()
                .code(ex.getMessage())
                .status(ex.getStatus())
                .build();
    }

    @Override
    public Class<CustomException> supportedClass() {
        return CustomException.class;
    }
}
