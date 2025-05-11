package tech.rket.payment.infrastructure.dto.attempt;

import tech.rket.payment.domain.attempt.Attempt;
import tech.rket.payment.domain.attempt.AttemptStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttemptWebhook {
    private final AttemptStatus status;
    private final String reference;
    private final String description;
    private final String sign;

    public static AttemptWebhook of(Attempt attempt) {
        return AttemptWebhook.builder()
                .description(attempt.getDescription())
                .reference(attempt.getTransaction().getReference())
                .status(attempt.getStatus())
                .build();
    }
}
