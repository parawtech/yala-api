package tech.rket.payment.infrastructure.dto.attempt;

import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptVerifyStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AttemptVerify extends AttemptResult {
    private final AttemptVerifyStatus status;
}
