package tech.rket.payment.infrastructure.dto.attempt;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class AttemptResult {
    private final Long reference;
    private String description;
}
