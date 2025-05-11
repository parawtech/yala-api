package tech.rket.payment.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jdro.payment")
public class PaymentConfig {
    private Duration minimumTimeWaitForMakingVerifyStaged = Duration.ofMinutes(15);
}
