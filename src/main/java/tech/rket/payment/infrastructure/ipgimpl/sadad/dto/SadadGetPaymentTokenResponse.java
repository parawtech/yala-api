package tech.rket.payment.infrastructure.ipgimpl.sadad.dto;

import tech.rket.payment.infrastructure.ipgimpl.sadad.enums.SadadGetPaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SadadGetPaymentTokenResponse(
        @JsonProperty("ResCode")
        SadadGetPaymentStatus resCode,
        @JsonProperty("Token")
        String token,
        @JsonProperty("Description")
        String description) {
    public static String[] storableKeys() {
        return new String[]{"Token", "ResCode"};
    }
}