package tech.rket.payment.infrastructure.ipgimpl.sadad.dto;

import tech.rket.payment.infrastructure.dto.SingableData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SadadGetPaymentTokenRequest implements SingableData {
    @JsonProperty("TerminalId")
    private String terminalId;
    @JsonProperty("MerchantId")
    private String merchantId;
    @JsonProperty("Amount")
    private long amount;
    @JsonProperty("SignData")
    private String signData;
    @JsonProperty("ReturnUrl")
    private String returnUrl;
    @JsonProperty("LocalDateTime")
    private String localDateTime;
    @JsonProperty("OrderId")
    private String orderId;

    @JsonIgnore
    public String getSingableData() {
        return String.format("%s;%s;%s", terminalId, orderId, amount);
    }

}