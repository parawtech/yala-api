package tech.rket.shared.infrastructure.exception.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private String type;
    private String title;
    private String timestamp;

    public static ExceptionResponse create(String type, String title, String timestamp) {
        return new ExceptionResponse(type, title, timestamp);
    }
}
