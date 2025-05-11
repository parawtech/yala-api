package tech.rket.shared.infrastructure.exception.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class SubError {
    private String field;
    private String message;
}
