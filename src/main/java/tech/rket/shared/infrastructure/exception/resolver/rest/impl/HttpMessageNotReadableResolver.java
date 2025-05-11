package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class HttpMessageNotReadableResolver implements RestExceptionResolver<HttpMessageNotReadableException> {
    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull HttpMessageNotReadableException ex, @Nonnull WebRequest request) {
        return RestErrorMessage.builder()
                .code("httpMessage.isNotReadable")
                .status(422)
                .build();
    }

    @Override
    public Class<HttpMessageNotReadableException> supportedClass() {
        return HttpMessageNotReadableException.class;
    }
}
