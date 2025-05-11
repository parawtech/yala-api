package tech.rket.auth.domain.core.user.entity;


import tech.rket.auth.domain.core.tenant.Tenant;
import tech.rket.auth.domain.core.user.commands.SessionCreate;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class Membership {
    private Tenant tenant;
    private String role;
    private Set<Session> sessions;

    public static Membership build(Tenant tenant, String role) {
        return new Membership(tenant, role, new HashSet<>());
    }

    public Optional<Session> findSession(UUID sessionId) {
        return sessions.stream()
                .filter(s -> s.id().equals(sessionId))
                .findAny();
    }

    public Session createSession(SessionCreate sessionCreate) {
        Instant now = Instant.now();
        Instant expiredAt = now.plus(sessionCreate.accessTokenExpireSeconds(), ChronoUnit.SECONDS);
        Instant refreshableUntil = now.plus(sessionCreate.refreshTokenExpireSeconds(), ChronoUnit.SECONDS);
        Session session = new Session(
                UUID.randomUUID(),
                now,
                expiredAt,
                refreshableUntil);
        sessions.add(session);
        return session;
    }

    public Optional<Session> removeSession(UUID uuid) {
        return findSession(uuid)
                .stream().peek(s -> sessions.remove(s)).findFirst();
    }
}
