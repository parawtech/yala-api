package tech.rket.storage.infrastructure.event.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tech.rket.shared.infrastructure.log.JDLogger;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;
import tech.rket.storage.application.exception.StoredFileDoesNotFoundException;
import tech.rket.storage.domain.StoredFile;
import tech.rket.storage.domain.StoredFileEventPublisher;
import tech.rket.storage.domain.StoredFileRepository;
import tech.rket.storage.domain.event.StoredFileMarkedForDeleted;
import tech.rket.storage.domain.event.StoredFileSentForUpload;
import tech.rket.storage.domain.value_object.StoredFileAuthType;
import tech.rket.storage.domain.value_object.StoredFileStatus;
import tech.rket.storage.infrastructure.InputStreamInMemoryStorage;
import tech.rket.storage.infrastructure.s3.StorageS3Repository;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StoredFileEventSpringSubscriber {
    private static final Logger log = JDLogger.getLogger(StoredFileEventSpringSubscriber.class).build();
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();

    private final StorageS3Repository s3;
    private final StoredFileRepository repository;
    private final StoredFileEventPublisher publisher;
    private final InputStreamInMemoryStorage inputStreamInMemoryStorage;

    @EventListener(StoredFileSentForUpload.class)
    @Async
    public void upload(StoredFileSentForUpload event) {
        StoredFile storedFile = repository.findById(event.id()).orElseThrow(() -> new StoredFileDoesNotFoundException(event.id()));
        InputStream stream = inputStreamInMemoryStorage.findById(event.id()).orElseThrow();
        Map<String, String> map = new HashMap<>();

        map.put("authType", storedFile.getAuth().type().name());
        if (storedFile.getAuth().type() != StoredFileAuthType.PRIVATE &&
                storedFile.getAuth().type() != StoredFileAuthType.PUBLIC) {
            map.put("tenant", "" + storedFile.getAuth().tenantId());
            if (storedFile.getAuth().authValues() != null && !storedFile.getAuth().authValues().isEmpty()) {
                map.put("authValues", String.join(",", storedFile.getAuth().authValues().toArray(new String[0])));
            }
        }
        map.put("uploader", storedFile.getUser().toString());
        map.put("id", storedFile.getId().toString());
        map.put("time", storedFile.getCreatedTime().toString());
        addMetadata(storedFile.getMetadata(), map);
        s3.upload(storedFile.getAuth(), storedFile.getId(), stream, storedFile.getSize(), storedFile.getMimeType().getMimeType(), map);
        storedFile.setAsUploaded();
        repository.save(storedFile);
        publisher.publish(storedFile);
    }

    private void addMetadata(Map<String, Object> storedFileMetadata, Map<String, String> objectMetadata) {
        for (Map.Entry<String, Object> metadata : storedFileMetadata.entrySet()) {
            try {
                objectMetadata.put("metadata_" + metadata.getKey(), OBJECT_MAPPER.writeValueAsString(metadata.getValue()));
            } catch (JsonProcessingException e) {
                log.warn("Cannot add metadata {} value {} due to {}.", metadata.getKey(), metadata.getValue(), e.getMessage(), e);
            }
        }
    }


    @EventListener(StoredFileMarkedForDeleted.class)
    @Async
    public void delete(StoredFileMarkedForDeleted event) {
        StoredFile storedFile = repository.findById(event.id()).orElseThrow(() -> new StoredFileDoesNotFoundException(event.id()));
        if (storedFile.getStatus() == StoredFileStatus.UPLOADED) {
            s3.delete(storedFile.getAuth(), storedFile.getId());
        }
        repository.deleteById(storedFile.getId());
    }
}
