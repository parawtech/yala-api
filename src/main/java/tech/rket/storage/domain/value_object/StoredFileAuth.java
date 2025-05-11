package tech.rket.storage.domain.value_object;

import tech.rket.shared.core.domain.DomainObject;
import jakarta.annotation.Nullable;

import java.util.List;

public record StoredFileAuth(
        StoredFileAuthType type,
        Long userId,
        Long tenantId,
        List<String> authValues)
        implements DomainObject.ValueObject {
    public StoredFileAuth(@Nullable StoredFileAuthType type, @Nullable Long userId, @Nullable Long tenantId, @Nullable List<String> authValues) {

        type = type == null ? StoredFileAuthType.PUBLIC : type;
        this.type = type;

        if (type == StoredFileAuthType.PUBLIC) {
            this.userId = null;
            this.tenantId = null;
            this.authValues = null;
            return;
        }

        switch (type) {
            case PRIVATE: {
                if (userId == null) {
                    throw new IllegalArgumentException("Auth information is not sufficient enough.");
                }
                this.userId = userId;
                this.tenantId = null;
                this.authValues = null;
                break;
            }
            case MEMBERSHIP: {
                if (userId == null || tenantId == null) {
                    throw new IllegalArgumentException("Auth information is not sufficient enough.");
                }
                this.userId = userId;
                this.tenantId = tenantId;
                this.authValues = null;
                break;
            }
            case MEMBERS: {
                if (tenantId == null) {
                    throw new IllegalArgumentException("Auth information is not sufficient enough.");
                }
                this.userId = null;
                this.tenantId = tenantId;
                this.authValues = null;
                break;
            }
            case ROLE_BASED, PERMISSION_BASED: {
                if (authValues == null || authValues.isEmpty() || tenantId == null) {
                    throw new IllegalArgumentException("Auth information is not sufficient enough.");
                }

                authValues.sort(String.CASE_INSENSITIVE_ORDER);
                this.userId = null;
                this.tenantId = tenantId;
                this.authValues = authValues;
                break;
            }
            default: {
                throw new IllegalStateException("Shall not pass here");
            }
        }
    }

    public boolean isPublic() {
        return type() == StoredFileAuthType.PUBLIC;
    }

    public boolean canAccess(Long requestedTenantId, Long requestedUserId, List<String> requestedAuthValues) {
        if (isPublic()) {
            return true;
        }
        if (requestedTenantId == null || requestedUserId == null) {
            return false;
        } else if (type() == StoredFileAuthType.PRIVATE && requestedUserId.equals(userId)) {
            return true;
        } else if (requestedTenantId.equals(tenantId)) {
            if (type == StoredFileAuthType.MEMBERSHIP && userId.equals(requestedUserId)) {
                return true;
            } else return (type() == StoredFileAuthType.ROLE_BASED || type() == StoredFileAuthType.PERMISSION_BASED) && requestedAuthValues.stream().anyMatch(authValues::contains);
        }
        return false;
    }
}
