package tech.rket.payment.infrastructure.ipgimpl.sadad.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter(AccessLevel.PRIVATE)
public enum SadadVerifyTokenStatus {
    PAID(0),
    FAILED(-1);

    private final Integer value;

    @JsonCreator
    public static SadadVerifyTokenStatus fromCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getValue().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                                String.format("SadadVerifyTokenStatus is not found with value: %d", code)
                        )
                );
    }
}
