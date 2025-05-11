package tech.rket.payment.infrastructure.ipgimpl.sadad;

import lombok.Data;

import java.util.Map;

@Data
public class SadadConfig {
    private String baseUrl;
    private String callbackUrl;
    private String merchantId;
    private String terminalId;
    private String redirectUrl;
    private String signKey;

    public static SadadConfig of(Map<String, Object> map) {
        SadadConfig sadadConfig = new SadadConfig();
        sadadConfig.setBaseUrl((String) map.get("base_uri"));
        sadadConfig.setMerchantId((String) map.get("merchant_id"));
        sadadConfig.setTerminalId((String) map.get("terminal_id"));
        sadadConfig.setCallbackUrl((String) map.get("callback_url"));
        sadadConfig.setSignKey((String) map.get("sign_key"));
        sadadConfig.setRedirectUrl((String) map.get("redirect_uri"));
        return sadadConfig;
    }
}