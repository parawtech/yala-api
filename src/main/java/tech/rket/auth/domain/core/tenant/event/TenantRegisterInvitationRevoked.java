package tech.rket.auth.domain.core.tenant.event;

import tech.rket.auth.domain.core.tenant.entity.TenantRegisterInvitation;
import tech.rket.shared.core.domain.event.DomainEvent;

import java.time.Instant;

public record TenantRegisterInvitationRevoked(Long id, Instant time,
                                              TenantRegisterInvitation invitation) implements DomainEvent<Long> {
}
