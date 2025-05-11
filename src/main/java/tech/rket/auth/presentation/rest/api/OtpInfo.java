package tech.rket.auth.presentation.rest.api;

import java.time.Instant;

public record OtpInfo(Integer mobileOtpLength, Instant expireAt) {
}
