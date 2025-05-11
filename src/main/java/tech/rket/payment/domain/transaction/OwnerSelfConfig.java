package tech.rket.payment.domain.transaction;

import tech.rket.payment.domain.shared.IPG;
import lombok.Data;

@Data
public class OwnerSelfConfig {
    private String callbackUrl;
    private String webhookUrl;
    private boolean webhookCallAfterCallback;
    private IPG suitableIPG;
    private String signKey;

    public void validate() {
        if (callbackUrl == null || callbackUrl.isBlank()) {
            throw new IllegalStateException();
        }
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalStateException();
        }
    }
}
