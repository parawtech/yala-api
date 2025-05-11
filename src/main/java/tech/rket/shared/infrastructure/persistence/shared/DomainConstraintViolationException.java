package tech.rket.shared.infrastructure.persistence.shared;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.icu.text.MessageFormat;
import lombok.Getter;
import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import tech.rket.shared.infrastructure.exception.Problem;
import tech.rket.shared.infrastructure.i18n.Translator;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;

import java.util.*;

@Getter
@Problem(details = {"errorDetails"}, status = "BAD_REQUEST")
public class DomainConstraintViolationException extends RuntimeException {
    private final List<DomainConstraintViolation> errors;
    private final List<Object> errorDetails = new ArrayList<>();
    private static final ObjectMapper objectMapper = ObjectMapperGenerator.jsonMapper()
            .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);

    public DomainConstraintViolationException(List<DomainConstraintViolation> result) {
        Objects.requireNonNull(result);
        errors = result;
        for (DomainConstraintViolation error : result) {
            Objects.requireNonNull(error);
            Objects.requireNonNull(error.code());
            if (error.code().isBlank()) {
                throw new NullPointerException();
            }
            String txt = Translator.text(error.code());
            txt = txt == null ? error.message() : txt;
            Objects.requireNonNull(txt);
            Object errorDetail = txt;
            if (error instanceof DomainConstraintViolation.Parameterized parameterized) {
                errorDetail = MessageFormat.format(txt, parameterized.getParameters().toArray());
            } else if (error instanceof DomainConstraintViolation.NamedParameterized namedParameterized) {
                Map<String, Object> map = new HashMap<>();
                txt = MessageFormat.format(txt, namedParameterized.getParameters());
                map.put("message", txt);
                map.put("parameters", namedParameterized.getParameters());
                errorDetail = map;
            }
            errorDetails.add(errorDetail);
        }

        try {
            String format = "There {0,plural, one {is one error} other {are # errors}}: ";
            format = MessageFormat.format(format, errorDetails.size());
            PersistenceMapper.set(this, "detailMessage", format + objectMapper.writeValueAsString(errorDetails));
        } catch (JsonProcessingException e) {
        }
    }
}
