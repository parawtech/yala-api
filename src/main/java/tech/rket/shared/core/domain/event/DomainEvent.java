package tech.rket.shared.core.domain.event;

import tech.rket.shared.core.shared.HasId;

import java.time.Instant;

public interface DomainEvent<DOMAIN_ID> extends HasId.Record<DOMAIN_ID> {
    default String name() {
        return this.getClass().getSimpleName();
    }

    Instant time();

    default Instant getTime() {
        return time();
    }

    default String getName() {
        return this.name();
    }
}
