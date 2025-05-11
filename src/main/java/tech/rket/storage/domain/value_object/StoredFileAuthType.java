package tech.rket.storage.domain.value_object;

import tech.rket.shared.core.domain.DomainObject;

public enum StoredFileAuthType implements DomainObject.ValueObject {
    PUBLIC, PRIVATE, ROLE_BASED, PERMISSION_BASED, MEMBERS, MEMBERSHIP
}
