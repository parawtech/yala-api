package tech.rket.payment.infrastructure.dto.attempt;

import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptSettlementStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class AttemptSettlement extends AttemptResult {
    private final String url;
    private final AttemptSettlementStatus status;
}
