package tech.rket.payment.infrastructure.dto.transaction;

import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.domain.transaction.TransactionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {
    @NotNull
    private String reference;
    @Min(10000)
    private BigDecimal amount;
    private IPG ipg;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TransactionStatus status;
}
