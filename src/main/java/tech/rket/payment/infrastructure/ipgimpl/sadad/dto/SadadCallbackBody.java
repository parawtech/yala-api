package tech.rket.payment.infrastructure.ipgimpl.sadad.dto;

import tech.rket.payment.infrastructure.ipgimpl.sadad.enums.SadadCallbackStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SadadCallbackBody(
        @JsonProperty("ResCode")
        SadadCallbackStatus status,
        @JsonProperty("OrderId")
        Long reference,
        @JsonProperty("Token")
        String token) {
}