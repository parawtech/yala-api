package tech.rket.auth.infrastructure.scheduling.user;


import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.rket.auth.infrastructure.persistence.user.repository.InvitationEntityRepository;
import tech.rket.auth.infrastructure.persistence.user.repository.SessionEntityRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "auth.scheduling.enable", havingValue = "true")
public class UserRepositoryScheduling {
    private final SessionEntityRepository sessionEntityRepository;
    private final InvitationEntityRepository invitationEntityRepository;
    @Value("${auth.scheduling.delete-extinct-sessions.enable}")
    private boolean enableDeleteExtinctSessions;
    @Value("${auth.scheduling.expire-extinct-invitations.enable}")
    private boolean enableExpireExtinctInvitations;


    @Scheduled(fixedDelay = 900_000, initialDelay = 300_000)
    @SchedulerLock(name = "auth.deleteExtinctSessions", lockAtMostFor = "30m", lockAtLeastFor = "15m")
    public void deleteExtinctSessions() {
        if (!enableDeleteExtinctSessions) {
            return;
        }
        Instant now = Instant.now();
        sessionEntityRepository.deleteByRefreshableUntilIsLessThan(now);
    }

    @Scheduled(fixedDelay = 900_000, initialDelay = 300_000)
    @SchedulerLock(name = "auth.expireExtinctInvitations", lockAtMostFor = "30m", lockAtLeastFor = "15m")
    public void expireExtinctInvitations() {
        if (!enableExpireExtinctInvitations) {
            return;
        }
        Instant now = Instant.now();
        invitationEntityRepository.expireExtinctInvitations(now);
    }
}
