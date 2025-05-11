package tech.rket.storage.domain.value_object;

import tech.rket.shared.core.domain.DomainObject;

public enum StoredFileStatus implements DomainObject.ValueObject {
    INSTANTIATED, SENT_FOR_UPLOAD, UPLOADED, MARK_FOR_DELETE
}
