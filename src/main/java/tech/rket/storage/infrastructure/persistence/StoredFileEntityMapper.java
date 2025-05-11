package tech.rket.storage.infrastructure.persistence;

import tech.rket.shared.infrastructure.persistence.mapper.DomainPersistenceMapper;
import tech.rket.shared.infrastructure.persistence.mapper.PersistenceMapper;
import tech.rket.shared.infrastructure.persistence.shared.DomainConstraintViolationException;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.command.StoreFileInstantiate;
import tech.rket.storage.domain.value_object.StoredFileAuth;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public final class StoredFileEntityMapper implements DomainPersistenceMapper<StoredFileEntity, StoredFile> {
    public static final StoredFileEntityMapper INSTANCE = new StoredFileEntityMapper();

    private StoredFileEntityMapper() {
    }

    @Override
    public StoredFile convert(StoredFileEntity entity) {
        StoredFileAuth storedFileAuth = new StoredFileAuth(entity.getAuth(), entity.getUserId(), entity.getTenant(), convert(entity.getAuthValues()));
        StoredFile storedFile = StoredFile.of(
                        new StoreFileInstantiate(
                                entity.getId(),
                                entity.getTenant(),
                                entity.getUserId(),
                                entity.getCharacteristic(),
                                entity.getFileKey(),
                                entity.getFileSize(),
                                entity.getMimeType(),
                                storedFileAuth,
                                entity.getParent() != null ? convert(entity.getParent()) : null,
                                entity.getVariantKeyName()
                        )
                ).
                throwIfFailure(DomainConstraintViolationException::new)
                .value();
        PersistenceMapper.set(storedFile, "status", entity.getStatus());
        PersistenceMapper.set(storedFile, "metadata", entity.getMetadataList());
        PersistenceMapper.set(storedFile, "createdTime", entity.getCreatedDate());
        PersistenceMapper.set(storedFile, "events", new LinkedHashSet<>());
        return storedFile;
    }

    public List<String> convert(String authValues) {
        if (authValues == null) {
            return null;
        }
        return new ArrayList<>(List.of(authValues.split(",")));
    }

    public StoredFileEntity convert(StoredFile value) {
        return new StoredFileEntity()
                .setCharacteristic(value.getCharacteristic())
                .setFileKey(value.getKey())
                .setFileSize(value.getSize())
                .setMimeType(value.getMimeType())
                .setTenant(value.getTenant())
                .setUserId(value.getUser())
                .setAuth(value.getAuth().type())
                .setAuthValues(convert(value.getAuth().authValues()))
                .setParent(value.getParent() == null ? null : convert(value))
                .setVariantKeyName(value.getVariantKey())
                .setMetadataList(value.getMetadata())
                .setStatus(value.getStatus())
                .setId(value.getId());
    }

    public String convert(List<String> list) {
        return list == null || list.isEmpty() ? null : String.join(",", list.toArray(new String[0]));
    }

    @Override
    public StoredFileEntity update(StoredFileEntity storedFileEntity, StoredFile val) {
        storedFileEntity.setAuth(val.getAuth().type())
                .setAuthValues(convert(val.getAuth().authValues()))
                .setParent(val.getParent() == null ? null : this.convert(val.getParent()))
                .setVariantKeyName(val.getVariantKey())
                .setStatus(val.getStatus())
                .setMetadataList(val.getMetadata())
                .setFileSize(val.getSize())
                .setMimeType(val.getMimeType());
        return storedFileEntity;
    }
}
