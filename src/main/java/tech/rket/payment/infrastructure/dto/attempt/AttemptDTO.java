package tech.rket.payment.infrastructure.dto.attempt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttemptDTO {
    private Map<String, Object> config;
    private Long reference;
    private BigDecimal amount;
}
