package tech.rket.auth.domain.core.user.entity;


import java.time.Instant;
import java.util.UUID;

public record Session(UUID id,
                      Instant startedAt,
                      Instant expiredAt,
                      Instant refreshableUntil) {

}
