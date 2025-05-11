package tech.rket.auth.infrastructure.persistence.user.entity;

import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_user_membership_session")
@NoArgsConstructor
@Getter
public class SessionEntity extends BaseEntity {
    @Id
    @JID(group = 0, type = 6)
    private Long id;
    private UUID uniqueId;
    @ManyToOne(fetch = FetchType.EAGER)
    private MembershipEntity membership;
    private Instant startedAt;
    private Instant expiredAt;
    private Instant refreshableUntil;

    public static SessionEntity build(UUID uniqueId, MembershipEntity membership, Instant startAt, Instant expiredAt, Instant refreshableUntil) {
        SessionEntity session = new SessionEntity();
        session.uniqueId = uniqueId;
        session.membership = membership;
        session.startedAt = startAt;
        session.expiredAt = expiredAt;
        session.refreshableUntil = refreshableUntil;
        return session;
    }
}
