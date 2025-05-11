package tech.rket.shared.infrastructure.exception;

import tech.rket.shared.infrastructure.exception.models.ErrorResponse;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.exception.rest.*;
import tech.rket.shared.infrastructure.log.JDLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionAdvice {
    private final Logger log = JDLogger.getLogger(RestExceptionAdvice.class, Map.of("category", "ADVICE"));
    @ExceptionHandler({
            Exception.class,
            RuntimeException.class,
            BadRequestException.class,
            InternalServerErrorException.class,
            NotFoundException.class,
            NotAvailableException.class,
            ForbiddenException.class,
            DuplicateException.class,
            CustomException.class,
            IllegalStateException.class,
            IllegalArgumentException.class,
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
            AccessDeniedException.class
    })
    public ResponseEntity<Object> handle(Exception ex, WebRequest webRequest) throws Exception {
        HttpServletResponse response = ((ServletWebRequest) webRequest).getResponse();
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        if (response != null && response.isCommitted()) {
            return null;
        }


        RestErrorMessage restErrorMessage = RestExceptionResolver.get(ex.getClass())
                .resolveException(ex, webRequest);
        if (restErrorMessage == null) {
            IllegalStateException exception = new IllegalStateException("Cannot Create a ErrorResponse");
            return handle(exception, webRequest);
        }
        ErrorResponse body = ResponseBodyFactory.create(restErrorMessage, ex, request);
        Level level = Level.ERROR;
        if (restErrorMessage.getStatus() >= 400 && restErrorMessage.getStatus() < 500) {
            level = Level.INFO;
        } else if (restErrorMessage.getStatus() == 503 || restErrorMessage.getStatus() == 504) {
            level = Level.WARN;
        }

        log.atLevel(level).log("Error Occurred when handling request: {}", ex.getMessage(), ex);
        return ResponseEntity.status(restErrorMessage.getStatus())
                .headers(restErrorMessage.getHeaders())
                .body(body);
        }
}
