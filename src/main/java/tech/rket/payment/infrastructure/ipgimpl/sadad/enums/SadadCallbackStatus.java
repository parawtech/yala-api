package tech.rket.payment.infrastructure.ipgimpl.sadad.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum SadadCallbackStatus {
    IPG_SUCCEEDED(0), ERROR(-1);
    private final Integer code;

    @JsonCreator
    public static SadadCallbackStatus fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                                String.format("SadadCallbackStatus is not found with value: %d", code)
                        )
                );
    }

}
