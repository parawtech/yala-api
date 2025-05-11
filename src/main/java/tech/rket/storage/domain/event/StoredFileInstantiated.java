package tech.rket.storage.domain.event;

import tech.rket.shared.core.domain.event.DomainEvent;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuth;
import tech.rket.storage.domain.value_object.StoredFileStatus;

import java.time.Instant;

public record StoredFileInstantiated(
        Long id,
        Instant time,
        Long tenant,
        Long user,
        String characteristic,
        String key,
        StoredFileAuth auth,
        StoredFileStatus status,
        Long size,
        MimeType mimeType,
        Long parentId,
        String variantKey
)
        implements DomainEvent<Long> {
}
