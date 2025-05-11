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
public class SadadVerifyTokenRequest implements SingableData {
    @JsonProperty("Token")
    private String token;
    @JsonProperty("SignData")
    private String signData;

    @JsonIgnore
    public String getSingableData() {
        return token;
    }

}