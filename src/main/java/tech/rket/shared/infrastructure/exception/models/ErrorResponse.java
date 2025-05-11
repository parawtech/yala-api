package tech.rket.shared.infrastructure.exception.models;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.ProblemDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class ErrorResponse extends ProblemDetail {
    private final String traceId = generateTraceId();
    private String code;
    private int status;
    private LocalDateTime timestamp;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<Object> details;
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> properties;

    @JsonProperty("properties")
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    private static String generateTraceId() {
        Transaction transaction = ElasticApm.currentTransaction();
        String trace = transaction.getTraceId();
        return trace.isBlank() ? null : trace;
    }
}
