package tech.rket.payment.service.transaction;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class AttemptSchedule {
    private final TransactionService service;

    @Value("${payment.scheduling.verifying-attempt.enable}")
    private boolean enableVerifyingAttempt;

    @Scheduled(fixedDelay = 5_000)
    @SchedulerLock(name = "payment.attemptSchedule", lockAtMostFor = "10s", lockAtLeastFor = "5s")
    @Transactional
    public void verifyingAttempt() {
        if (!enableVerifyingAttempt) {
            return;
        }
        service.verifyingAttempt();
        service.verifyingStaged();
        service.notifyingAttempts();
    }
}
