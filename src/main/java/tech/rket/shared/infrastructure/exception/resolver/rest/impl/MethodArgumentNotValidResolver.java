package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.i18n.Translator;

import java.util.HashMap;
import java.util.Map;

@Component
public class MethodArgumentNotValidResolver implements RestExceptionResolver<MethodArgumentNotValidException> {
    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull MethodArgumentNotValidException ex, @Nonnull WebRequest request) {
        RestErrorMessage restErrorMessage = RestErrorMessage.builder()
                .code("methodArgument.isNotValid")
                .status(400)
                .build();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", e.getField());
                    map.put("message", e.getDefaultMessage());
                    map.put("rejectedValue", e.getRejectedValue());
                    restErrorMessage.getDetails().add(map);
                });
        ex.getBindingResult().getGlobalErrors()
                .forEach(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("field", e.getObjectName());
                    map.put("message", e.getDefaultMessage());
                    restErrorMessage.getDetails().add(map);
                });
        return restErrorMessage;
    }

    @Override
    public Class<MethodArgumentNotValidException> supportedClass() {
        return MethodArgumentNotValidException.class;
    }
}
