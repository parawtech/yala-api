package tech.rket.payment.infrastructure.feign.webhook;


import tech.rket.payment.infrastructure.dto.attempt.AttemptWebhook;
import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

public interface WebhookFeign {
    @RequestLine("POST ")
    @Headers("Content-Type: application/json")
    void call(@RequestBody AttemptWebhook body);
}
