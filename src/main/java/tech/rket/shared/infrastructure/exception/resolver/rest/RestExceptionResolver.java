package tech.rket.shared.infrastructure.exception.resolver.rest;

import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.ExceptionResolver;
import tech.rket.shared.infrastructure.exception.resolver.rest.impl.ManagedExceptionResolver;
import tech.rket.shared.infrastructure.exception.resolver.rest.impl.RestDefaultResolver;
import tech.rket.shared.infrastructure.exception.rest.BadRequestException;
import tech.rket.shared.infrastructure.exception.rest.InternalServerErrorException;
import tech.rket.shared.infrastructure.exception.rest.NotFoundException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Optional;

public interface RestExceptionResolver<T extends Throwable> extends ExceptionResolver<T, RestErrorMessage> {
    default String protocol() {
        return "rest";
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> Optional<RestExceptionResolver<T>> find(Class<T> tclass) {
        return ExceptionResolver.find(tclass, "rest")
                .map(e -> (RestExceptionResolver<T>) e);
    }

    List<Class<?>> MANAGED_EXCEPTIONS = List.of(
            BadRequestException.class,
            InternalServerErrorException.class,
            NotFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            ServletRequestBindingException.class,
            MethodArgumentNotValidException.class,
            HandlerMethodValidationException.class,
            NoHandlerFoundException.class,
            NoResourceFoundException.class,
            AsyncRequestTimeoutException.class,
            ErrorResponseException.class,
            MaxUploadSizeExceededException.class,
            ConversionNotSupportedException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodValidationException.class,
            BindException.class,
            AccessDeniedException.class,
            NestedRuntimeException.class
    );

    @SuppressWarnings("unchecked")
    static <T extends Throwable> RestExceptionResolver<T> get(Class<T> tclass) {
        Optional<RestExceptionResolver<T>> t = RestExceptionResolver.find(tclass);
        if (t.isEmpty()) {
            if (MANAGED_EXCEPTIONS.contains(tclass) || MANAGED_EXCEPTIONS.stream().anyMatch(e -> e.isAssignableFrom(tclass))) {
                return (RestExceptionResolver<T>) new ManagedExceptionResolver();
            } else {
                return (RestExceptionResolver<T>) new RestDefaultResolver();
            }
        } else {
            return t.get();
        }
    }
}
