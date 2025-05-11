package tech.rket.auth.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.rket.auth.application.user.OtpSender;
import tech.rket.auth.presentation.rest.api.OtpInfo;
import tech.rket.shared.infrastructure.redis.CacheService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class OtpSenderImpl implements OtpSender {
    @Value("${auth.otp.mobile.lifetime}")
    private Duration mobileLifetime;
    @Value("${auth.otp.mobile.length}")
    private Integer mobileLength;
    @Value("${auth.otp.mobile.minimum-time-to-reload")
    private Duration mobileMinimumTimeToReload;
    private final CacheService cacheService;

    @Override
    public OtpInfo sendOtpToMobile(String mobile) {
        long minimumTimeToReload = mobileMinimumTimeToReload.toMillis();
        String key = mobilePrefix(mobile);
        OtpInfo info = (OtpInfo) cacheService.get(key)
                .orElse(null);
        if (info != null && info.expireAt().minus(minimumTimeToReload, ChronoUnit.MILLIS).isBefore(Instant.now())) {
            return info;
        }
        // TODO send with message
        boolean shouldReplace = info != null;
        Instant instant = Instant.now().plus(mobileLifetime.toMillis(), ChronoUnit.MILLIS);
        info = new OtpInfo(mobileLength, instant);
        if (shouldReplace) {
            cacheService.replace(key, info, mobileLifetime)
        } else {
            cacheService.set(key, info, mobileLifetime);
        }
        return info;
    }

    @Override
    public boolean checkMobileOtp(String mobile, String otp) {
        return false;
    }

    @Override
    public void evictMobileOtp(String mobile, String otp) {

    }

    @Override
    public boolean checkEmailOtp(String email, String otp) {
        throw new NotImplementedException();
    }

    @Override
    public void evictEmailOtp(String email, String otp) {
        throw new NotImplementedException();
    }

    @Override
    public OtpInfo sendOtpToEmail(String email) {
        throw new NotImplementedException();
    }

    private String mobilePrefix(String mobile) {
        return String.format("AUTH.OTP.MOBILE.%s", mobile);
    }

    private String emailPrefix(String email) {
        email = URLDecoder.decode(email, StandardCharsets.UTF_8);
        return String.format("AUTH.OTP.EMAIL.%s", email);
    }
}
