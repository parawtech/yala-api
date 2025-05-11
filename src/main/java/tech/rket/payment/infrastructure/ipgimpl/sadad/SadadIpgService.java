package tech.rket.payment.infrastructure.ipgimpl.sadad;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import tech.rket.payment.domain.owner.Owner;
import tech.rket.payment.domain.owner.OwnerConfig;
import tech.rket.payment.domain.shared.IPG;
import tech.rket.payment.domain.shared.JDAdditionInfo;
import tech.rket.payment.infrastructure.dto.attempt.AttemptCallback;
import tech.rket.payment.infrastructure.dto.attempt.AttemptSettlement;
import tech.rket.payment.infrastructure.dto.attempt.AttemptVerify;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptCallbackStatus;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptSettlementStatus;
import tech.rket.payment.infrastructure.dto.attempt.enums.AttemptVerifyStatus;
import tech.rket.payment.infrastructure.error.AttemptCallbackException;
import tech.rket.payment.infrastructure.error.AttemptSettlementException;
import tech.rket.payment.infrastructure.error.AttemptVerifyException;
import tech.rket.payment.infrastructure.feign.ApiClientBean;
import tech.rket.payment.infrastructure.ipgimpl.sadad.dto.*;
import tech.rket.payment.infrastructure.ipgimpl.sadad.enums.SadadCallbackStatus;
import tech.rket.payment.infrastructure.ipgimpl.sadad.enums.SadadGetPaymentStatus;
import tech.rket.payment.service.ipg.IpgService;
import tech.rket.shared.infrastructure.log.JDLogger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static tech.rket.payment.infrastructure.dto.attempt.enums.AttemptVerifyStatus.*;

@Component
@RequiredArgsConstructor
public class SadadIpgService implements IpgService {
    private static final Logger log = JDLogger.getLogger(SadadIpgService.class, Map.of("category", "PAYMENT"));

    private final ApiClientBean apiClientBean;
    private final ObjectMapper objectMapper;

    @Override
    public AttemptSettlement settlement(Owner owner, Long reference, BigDecimal amount, JDAdditionInfo additionalInfo) {
        Map<String, Object> map = owner.getOwnerConfig(supportedIPG())
                .map(OwnerConfig::getValue)
                .orElseGet(HashMap::new);
        var sadadConfig = SadadConfig.of(map);
        var request = SadadGetPaymentTokenRequest.builder()
                .amount(amount.longValue())
                .orderId(reference.toString())
                .merchantId(sadadConfig.getMerchantId())
                .terminalId(sadadConfig.getTerminalId())
                .returnUrl(sadadConfig.getCallbackUrl())
                .localDateTime(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").format(new Date()))
                .build();
        request.sign(sadadConfig.getSignKey());

        try {
            var response = sadadClient(sadadConfig).payment(request);
            additionalInfo.addAdditionalInfo("payment", response, SadadGetPaymentTokenResponse.storableKeys());
            if (response != null) {
                log.info("SADAD: SETTLEMENT: Attempt #{} settlement successfully called with status {}({}).", reference, response.resCode(), response.resCode().getCode());
                var builder = AttemptSettlement.builder()
                        .description(response.description())
                        .reference(reference);
                if (response.resCode() == SadadGetPaymentStatus.SUCCESSFUL_TRANSACTION) {
                    String url = sadadConfig.getRedirectUrl().replace("{Token}", response.token());
                    builder.status(AttemptSettlementStatus.REDIRECTABLE)
                            .url(url);
                } else {
                    builder.status(response.resCode().isTriable() ? AttemptSettlementStatus.TEMPORARY_ERROR : AttemptSettlementStatus.INVALID);
                }
                return builder.build();
            } else {
                log.warn("SADAD: SETTLEMENT: Attempt #{} settlement is null.", reference);
                throw new AttemptSettlementException("SADAD: Attempt #{} settlement is null.");
            }
        } catch (Exception e) {
            log.warn("SADAD: SETTLEMENT: Attempt #{} settlement failed due to {}.", reference, e.getMessage());
            throw new AttemptSettlementException("SADAD: Attempt settlement failed.", e);
        }
    }

    @Override
    public Long referenceFromCallback(Object o) {
        return callbackBody(o).reference();
    }

    private SadadCallbackBody callbackBody(Object o) {
        try {
            SadadCallbackBody callbackBody = objectMapper.convertValue(o, SadadCallbackBody.class);
            if (callbackBody != null) {
                log.debug("SADAD: CALLBACK: body is {}.", callbackBody);
                return callbackBody;
            } else {
                log.warn("SADAD: CALLBACK: body is null.");
                throw new AttemptCallbackException("SADAD: body is null.");
            }
        } catch (Exception e) {
            log.warn("SADAD: CALLBACK: body is not parseable due to {}", e.getMessage());
            throw new AttemptCallbackException("SADAD: body is not parseable.", e);
        }
    }

    @Override
    public AttemptCallback callback(Object o, JDAdditionInfo additionalInfo) {
        SadadCallbackBody callbackBody = callbackBody(o);
        additionalInfo.addAdditionalInfo("callback", callbackBody);
        if (callbackBody.status() == SadadCallbackStatus.IPG_SUCCEEDED) {
            log.debug("SADAD: CALLBACK: Attempt ${} payment successfully completed. (may not paid).", callbackBody.reference());
            return AttemptCallback.builder()
                    .status(AttemptCallbackStatus.VERIFIABLE)
                    .description(null)
                    .reference(callbackBody.reference())
                    .build();
        } else {
            log.debug("SADAD: CALLBACK: Attempt ${} payment is failed.", callbackBody.reference());
            return AttemptCallback.builder()
                    .status(AttemptCallbackStatus.FAILED)
                    .description(null)
                    .reference(callbackBody.reference())
                    .build();
        }
    }

    private SadadApiClient sadadClient(SadadConfig config) {
        return apiClientBean.apiClient(SadadApiClient.class, config.getBaseUrl());
    }

    @Override
    public AttemptVerify verify(Owner owner, Long reference, JDAdditionInfo additionalInfo) {
        Map<String, Object> map = owner.getOwnerConfig(supportedIPG())
                .map(OwnerConfig::getValue)
                .orElseGet(HashMap::new);
        SadadConfig sadadConfig = SadadConfig.of(map);

        String token = (String) additionalInfo.get("payment").get(additionalInfo.get("payment").size() - 1).get("Token");
        SadadVerifyTokenRequest verifyTokenRequest = SadadVerifyTokenRequest.builder()
                .token(token)
                .build();
        verifyTokenRequest.sign(sadadConfig.getSignKey());

        try {
            SadadVerifyTokenResponse response = sadadClient(sadadConfig).verify(verifyTokenRequest);
            additionalInfo.addAdditionalInfo("verify", response, SadadVerifyTokenResponse.storableKeys());
            if (response != null) {
                log.debug("SADAD: VERIFY: Attempt #{} verification successfully with status {}({}).", reference, response.status(), response.status().getCode());
                AttemptVerifyStatus status;
                if (response.status() == SadadGetPaymentStatus.SUCCESSFUL_TRANSACTION) {
                    status = PAID;
                } else if (response.status().isTriable()) {
                    status = VERIFYING;
                } else {
                    status = FAILED;
                }
                return AttemptVerify.builder()
                        .reference(reference)
                        .description(response.description())
                        .status(status)
                        .build();
            } else {
                throw new AttemptVerifyException("SADAD: Attempt verification is unsuccessful because body is null.");
            }
        } catch (Exception e) {
            log.warn("SADAD: VERIFY: Attempt #{} verification is unsuccessful due to {}.", reference, e.getMessage());
            throw new AttemptVerifyException("SADAD: Attempt verification is unsuccessful", e);
        }
    }

    @Override
    public IPG supportedIPG() {
        return IPG.SADAD;
    }
}
