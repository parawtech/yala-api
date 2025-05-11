package tech.rket.storage.domain.entity;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.storage.domain.value_object.StoredFileAuthType;

import java.util.Date;

public record StoredFileTemporaryUrl(Long id, StoredFileAuthType type, Date expire, String temporaryUrl)
        implements DomainObject.Entity.EntityRecord<Long> {
}