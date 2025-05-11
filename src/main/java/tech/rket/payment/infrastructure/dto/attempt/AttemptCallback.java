package tech.rket.payment.infrastructure.dto.attempt;

import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptCallbackStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AttemptCallback extends AttemptResult {
    private final AttemptCallbackStatus status;
}
