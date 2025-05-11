package tech.rket.auth.application.user;

import tech.rket.auth.presentation.rest.api.OtpInfo;

public interface OtpSender {
    OtpInfo sendOtpToMobile(String mobile);

    boolean checkMobileOtp(String mobile, String otp);

    void evictMobileOtp(String mobile,String otp);

    boolean checkEmailOtp(String email, String otp);

    void evictEmailOtp(String email, String otp);

    OtpInfo sendOtpToEmail(String email);
}
