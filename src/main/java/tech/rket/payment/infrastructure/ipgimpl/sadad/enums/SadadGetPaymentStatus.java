package tech.rket.payment.infrastructure.ipgimpl.sadad.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SadadGetPaymentStatus {
    SUCCESSFUL_TRANSACTION(0),
    MERCHANT_INACTIVE(3),
    MERCHANT_INVALID(23),
    TERMINAL_NOT_AUTHORIZED(58),
    CARD_AMOUNT_EXCEEDS_LIMIT(61),
    INVALID_PARAMETER_SEQUENCE(1000),
    INVALID_PARAMETERS(1001),
    MERCHANT_SYSTEM_ERROR(1002),
    INVALID_IP(1003),
    MERCHANT_INVALID_NUMBER(1004),
    ACCESS_ERROR(1005),
    IPG_SYSTEM_ERROR(1006),
    DUPLICATE_REQUEST(1011),
    INVALID_MERCHANT_INFO(1012),
    SWITCH_UNKNOWN_ERROR(1015),
    AMOUNT_EXCEEDS_DEFINED_LIMIT(1017),
    SYSTEM_DATE_TIME_ERROR(1018),
    MERCHANT_PAYMENT_NOT_AVAILABLE(1019),
    IPG_MERCHANT_DISABLED(1020),
    INVALID_RETURN_URL(1023),
    MERCHANT_INVALID_TIMESTAMP(1024),
    INVALID_SIGNATURE(1025),
    INVALID_TRANSACTION_ORDER_NUMBER(1026),
    INVALID_MERCHANT_NUMBER(1027),
    INVALID_TERMINAL_NUMBER(1028),
    INVALID_PAYMENT_IP(1029),
    INVALID_PAYMENT_DOMAIN(1030),
    TIME_EXPIRED(1031),
    CARD_PAYMENT_NOT_AVAILABLE_FOR_MERCHANT(1032),
    MERCHANT_SITE_PROBLEM(1033),
    MISSING_ADDITIONAL_INFO(1036),
    INVALID_MERCHANT_TERMINAL_NUMBER(1037),
    MERCHANT_INVALID_REQUEST(1053),
    INVALID_INPUT_VALUE(1055),
    SYSTEM_TEMPORARILY_DOWN(1056),
    INTERNET_PAYMENT_SERVICE_OFF(1058),
    UNIQUE_CODE_GENERATION_ERROR(1061),
    TRY_AGAIN(1064),
    UNSUCCESSFUL_CONNECTION(1065),
    PAYMENT_SERVICE_TEMPORARILY_DISABLED(1066),
    SYSTEM_TEMPORARILY_DOWN_FOR_UPDATE(1068),
    OPTIONAL_PARAMETER_ERROR(1072, false),
    INVALID_TRANSACTION_AMOUNT(1101, false),
    INVALID_TOKEN(1103),
    INVALID_SPLITTING_INFO(1104, false);
    private final Integer code;
    private final boolean triable;

    SadadGetPaymentStatus(Integer code) {
        this(code, true);
    }

    SadadGetPaymentStatus(Integer code, boolean triable) {
        this.code = code;
        this.triable = triable;
    }

    @JsonCreator
    public static SadadGetPaymentStatus fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                                String.format("SadadGetPaymentStatus is not found with value: %d", code)
                        )
                );
    }

}
