package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.i18n.Translator;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class ManagedExceptionResolver implements RestExceptionResolver<Exception> {
    private static final MessageSource MESSAGE_SOURCE = Translator.messageSource("problem");
    private static final ResponseEntityExceptionHandler HANDLER = new ResponseEntityExceptionHandler() {
        @Override
        public void setMessageSource(MessageSource messageSource) {
            super.setMessageSource(MESSAGE_SOURCE);
        }
    };

    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull Exception ex, @Nonnull WebRequest request) {
        ProblemDetail problemDetail;
        try {
            problemDetail = (ProblemDetail) HANDLER.handleException(ex, request).getBody();
            RestErrorMessage message = RestErrorMessage.builder()
                    .namedArguments(false)
                    .code(ex.getClass().getName())
                    .title(problemDetail.getTitle())
                    .detail(problemDetail.getDetail())
                    .status(problemDetail.getStatus())
                    .build();
            message.getRoot().put("properties", problemDetail.getProperties());
            return message;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public Class<Exception> supportedClass() {
        return Exception.class;
    }
}
