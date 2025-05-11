package tech.rket.shared.infrastructure.scheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
@ConditionalOnProperty(value = "shared.scheduling.enabled", havingValue = "true")
public class ShedLockConfig {
    public ShedLockConfig() {

    }
}