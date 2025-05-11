package tech.rket.storage.domain.entity;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.storage.domain.value_object.MimeType;

import java.io.InputStream;

public record StoredFileContent(Long id, Long size, MimeType mimeType, InputStream stream)
        implements DomainObject.Entity.EntityRecord<Long> {
}
