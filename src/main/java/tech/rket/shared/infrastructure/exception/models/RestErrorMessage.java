package tech.rket.shared.infrastructure.exception.models;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class RestErrorMessage {
    private final String code;
    private final String title;
    private final String detail;
    private final boolean namedArguments;
    private final int status;
    private final Map<String, Object> parameters = new LinkedHashMap<>();
    private final HttpHeaders headers = new HttpHeaders();
    private final Map<String, Object> root = new LinkedHashMap<>();
    private final List<Object> details = new ArrayList<>();
}
