package tech.rket.storage.infrastructure.persistence;

import tech.rket.shared.infrastructure.persistence.BaseEntity;
import tech.rket.shared.infrastructure.persistence.PersistedObject;
import tech.rket.storage.domain.value_object.MimeType;
import tech.rket.storage.domain.value_object.StoredFileAuthType;
import tech.rket.storage.domain.value_object.StoredFileStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import tech.rket.shared.infrastructure.model.id.JID;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import java.util.Map;

@Entity
@Table(name = "storage_file")
@Getter
@NoArgsConstructor
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
@JID(type = 1, group = 1)
public class StoredFileEntity extends BaseEntity implements PersistedObject<Long> {
    @Id
    private Long id;
    private String characteristic;
    private String fileKey;
    private Long tenant;
    private Long userId;
    @Enumerated(EnumType.STRING)
    public StoredFileAuthType auth;
    @Enumerated(EnumType.STRING)
    public StoredFileStatus status;
    private String authValues;
    private long fileSize;
    @Enumerated(EnumType.STRING)
    private MimeType mimeType;
    private String variantKeyName;
    @ManyToOne
    private StoredFileEntity parent;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> metadataList;
}
