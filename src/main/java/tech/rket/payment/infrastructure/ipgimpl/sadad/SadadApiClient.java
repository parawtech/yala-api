package tech.rket.payment.infrastructure.ipgimpl.sadad;

import tech.rket.payment.infrastructure.ipgimpl.sadad.dto.SadadGetPaymentTokenRequest;
import tech.rket.payment.infrastructure.ipgimpl.sadad.dto.SadadGetPaymentTokenResponse;
import tech.rket.payment.infrastructure.ipgimpl.sadad.dto.SadadVerifyTokenRequest;
import tech.rket.payment.infrastructure.ipgimpl.sadad.dto.SadadVerifyTokenResponse;
import feign.Headers;
import feign.RequestLine;

public interface SadadApiClient {
    @RequestLine("POST /api/v0/Request/PaymentRequest")
    @Headers("Content-Type: application/json")
    SadadGetPaymentTokenResponse payment(SadadGetPaymentTokenRequest request);

    @RequestLine("POST /api/v0/Advice/Verify")
    @Headers("Content-Type: application/json")
    SadadVerifyTokenResponse verify(SadadVerifyTokenRequest request);
}