package tech.rket.payment.infrastructure.ipgimpl.sadad.dto;

import tech.rket.payment.infrastructure.ipgimpl.sadad.enums.SadadGetPaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SadadVerifyTokenResponse(
        @JsonProperty("ResCode")
        SadadGetPaymentStatus status,
        long amount,
        @JsonProperty("Description")
        String description,
        @JsonProperty("RetrivalRefNo")
        String retrivalRefNo,
        @JsonProperty("SystemTraceNo")
        String systemTraceNo,
        @JsonProperty("OrderId")
        Long orderId) {

    public static String[] storableKeys() {
        return new String[]{"ResCode", "RetrivalRefNo", "RetrivalRefNo"};
    }
}