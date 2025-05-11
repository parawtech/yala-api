package tech.rket.payment.service.ipg;

import tech.rket.payment.domain.owner.Owner;
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
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;

public interface IpgService {
    AttemptSettlement settlement(Owner owner, Long reference, BigDecimal amount, JDAdditionInfo additionalInfo);

    default AttemptSettlement settlementSafe(Owner owner, Long reference, BigDecimal amount, JDAdditionInfo additionalInfo) {
        try {
            return settlement(owner, reference, amount, additionalInfo);
        } catch (AttemptSettlementException e) {
            return AttemptSettlement.builder()
                    .status(AttemptSettlementStatus.TEMPORARY_ERROR)
                    .reference(reference)
                    .description(null)
                    .build();
        }
    }

    Long referenceFromCallback(Object o);

    AttemptCallback callback(Object o, JDAdditionInfo additionalInfo);

    default AttemptCallback callbackSafe(Object o, JDAdditionInfo additionalInfo) {
        try {
            return callback(o, additionalInfo);
        } catch (AttemptCallbackException e) {
            return AttemptCallback.builder()
                    .status(AttemptCallbackStatus.VERIFIABLE)
                    .reference(referenceFromCallback(o))
                    .description(null)
                    .build();
        }
    }


    AttemptVerify verify(Owner owner, Long reference, JDAdditionInfo additionalInfo);

    default AttemptVerify verifySafe(Owner owner, Long reference, JDAdditionInfo additionalInfo) {
        try {
            return verify(owner, reference, additionalInfo);
        } catch (AttemptVerifyException e) {
            return AttemptVerify.builder()
                    .status(AttemptVerifyStatus.VERIFYING)
                    .reference(reference)
                    .description(null)
                    .build();
        }
    }

    IPG supportedIPG();

    @PostConstruct
    default void register() {
        if (supportedIPG() != null) {
            IpgFactory.IPG_SERVICE_MAP.put(supportedIPG(), this);
        }
    }
}
