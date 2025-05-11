package tech.rket.storage.domain;

import tech.rket.shared.core.domain.DomainObject;
import tech.rket.shared.core.domain.SharedAggregateRoot;
import tech.rket.shared.core.domain.result.DomainConstraintViolation;
import tech.rket.shared.core.domain.result.DomainResult;
import tech.rket.shared.core.query.QueryObject;
import tech.rket.storage.domain.command.*;
import tech.rket.storage.domain.event.*;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuth;
import tech.rket.storage.domain.value_object.StoredFileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StoredFile
        extends SharedAggregateRoot<Long>
        implements DomainObject.Entity.AggregateRoot<Long>, QueryObject<Long> {
    private final Long id;
    private final Long tenant;
    private final Long user;
    private final String characteristic;
    private final String key;
    private final Instant createdTime;
    private StoredFileAuth auth;
    private StoredFileStatus status;
    private Long size;
    private MimeType mimeType;
    private final Map<String, Object> metadata;
    private StoredFile parent;
    private String variantKey;

    public static DomainResult<StoredFile> of(StoreFileInstantiate command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }

        StoredFile storedFile = new StoredFile(
                command.id(),
                command.tenant(),
                command.user(),
                command.characteristic(),
                command.key(),
                Instant.now(),
                command.auth(),
                StoredFileStatus.INSTANTIATED,
                command.size(),
                command.mimeType(),
                new LinkedHashMap<>(),
                command.parent(),
                command.variantName()
        );
        var event = new StoredFileInstantiated(
                storedFile.id,
                storedFile.createdTime,
                storedFile.tenant,
                storedFile.user,
                storedFile.characteristic,
                storedFile.key,
                storedFile.auth,
                storedFile.status,
                storedFile.size,
                storedFile.mimeType,
                storedFile.parent == null ? null : storedFile.parent.id,
                storedFile.variantKey
        );
        storedFile.registerEvent(event);
        return DomainResult.success(storedFile);
    }

    public DomainResult<StoredFileSentForUpload> sentForUpload(StoredFileSendForUpload command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        if (status != StoredFileStatus.INSTANTIATED) {
            return DomainResult.fail(StoredFileConstraintViolation.STORED_FILE_IS_SENT_FOR_UPLOAD_ALREADY);
        } else if (this.mimeType != command.mimeType()) {
            return DomainResult.fail(StoredFileConstraintViolation.STORED_FILE_REGISTERED_MIME_TYPE_IS_DIFFERENT);
        }
        this.status = StoredFileStatus.SENT_FOR_UPLOAD;
        this.size = command.size();
        return DomainResult.success(new StoredFileSentForUpload(id, Instant.now())).peek(this::registerEvent);
    }

    public DomainResult<StoredFileUploaded> setAsUploaded() {
        if (status != StoredFileStatus.SENT_FOR_UPLOAD) {
            return DomainResult.fail(StoredFileConstraintViolation.STORED_FILE_DOES_NOT_SENT_FOR_UPLOAD);
        }
        status = StoredFileStatus.UPLOADED;
        return DomainResult.success(new StoredFileUploaded(id, Instant.now())).peek(this::registerEvent);
    }

    public DomainResult<StoredFileMarkedForDeleted> markForDelete() {
        if (status == StoredFileStatus.SENT_FOR_UPLOAD) {
            return DomainResult.fail(StoredFileConstraintViolation.STORED_FILE_IS_SENT_FOR_UPLOAD_ALREADY);
        } else if (status == StoredFileStatus.MARK_FOR_DELETE) {
            return DomainResult.fail(StoredFileConstraintViolation.STORED_FILE_IS_MARKED_FOR_DELETED_ALREADY);
        } else {
            status = StoredFileStatus.MARK_FOR_DELETE;
            return DomainResult.success(new StoredFileMarkedForDeleted(id, Instant.now())).peek(this::registerEvent);
        }
    }

    public DomainResult<StoredFileParentAssigned> assignParent(StoredFileParentAssign command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        if (command.parent() != null && command.parent().getId().equals(this.id)) {
            return DomainResult.fail(StoredFileConstraintViolation.FILE_CANNOT_BE_PARENT_OF_ITSELF);
        }
        if (command.parent() != null && command.parent().parent != null && command.parent().parent.getId().equals(this.id)) {
            return DomainResult.fail(StoredFileConstraintViolation.FILE_CANNOT_BE_CHILD_OF_ITS_CHILD);
        }
        this.parent = command.parent();
        this.variantKey = command.key();
        return DomainResult.success(new StoredFileParentAssigned(id, Instant.now(), parent.id, variantKey)).peek(this::registerEvent);
    }

    public DomainResult<StoredFileMetadataAdded> addMetadata(StoredFileMetadataAdd command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        metadata.put(command.key(), command.value());
        return DomainResult.success(new StoredFileMetadataAdded(id, Instant.now(), command.key(), command.value())).peek(this::registerEvent);
    }

    public DomainResult<StoredFileMetadataDeleted> removeMetadata(StoredFileMetadataDelete command) {
        List<DomainConstraintViolation> violations = command.validate();
        if (!violations.isEmpty()) {
            return DomainResult.fail(violations);
        }
        if (!metadata.containsKey(command.key())) {
            return DomainResult.fail(StoredFileConstraintViolation.METADATA_DOES_NOT_EXISTS);
        }
        Object value = metadata.remove(command.key());
        return DomainResult.success(new StoredFileMetadataDeleted(id, Instant.now(), command.key(), value)).peek(this::registerEvent);
    }
}
