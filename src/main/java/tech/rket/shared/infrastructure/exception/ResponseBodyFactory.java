package tech.rket.shared.infrastructure.exception;

import com.ibm.icu.text.MessageFormat;
import tech.rket.shared.infrastructure.exception.models.ErrorResponse;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.i18n.Translator;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.text.FieldPosition;
import java.util.Locale;

public class ResponseBodyFactory {
    private ResponseBodyFactory() {
    }

    public static ErrorResponse create(RestErrorMessage restErrorMessage, Throwable ex, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .status(restErrorMessage.getStatus())
                .code(restErrorMessage.getCode())
                .properties(restErrorMessage.getRoot())
                .details(restErrorMessage.getDetails())
                .build();
        response.setTitle(createMessage(restErrorMessage, ex, request));
        response.setInstance(URI.create(request.getRequestURI()));
        response.setDetail(restErrorMessage.getDetail());
        return response;
    }

    private static String createMessage(RestErrorMessage restErrorMessage, Throwable ex, HttpServletRequest request) {
        Locale locale = request.getLocale();
        if (restErrorMessage.getTitle() != null) {
            return restErrorMessage.getTitle();
        }
        if (restErrorMessage.getCode() == null) {
            return null;
        }
        String resourceText = getResourceText(locale, restErrorMessage.getCode());
        if (resourceText == null) {
            return ex.getMessage() == null || ex.getMessage().isBlank() ? "Unknown Error" : ex.getMessage();
        }
        if (!restErrorMessage.getParameters().isEmpty()) {
            MessageFormat mf = new MessageFormat(resourceText, locale);
            if (restErrorMessage.isNamedArguments()) {
                return mf.format(restErrorMessage.getParameters(), new StringBuffer(), new FieldPosition(0)).toString();
            } else {
                return mf.format(restErrorMessage.getParameters().values().toArray(), new StringBuffer(), new FieldPosition(0)).toString();
            }
        } else {
            return resourceText;
        }
    }

    private static String getResourceText(Locale locale, String code) {
        String message;
        if ((message = Translator.text(String.format("%s.title", code), locale)) != null) {
            return message;
        } else if ((message = Translator.text(code, locale)) != null) {
            return message;
        } else {
            return null;
        }
    }
}
