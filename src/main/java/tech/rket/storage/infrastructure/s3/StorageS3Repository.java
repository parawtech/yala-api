package tech.rket.storage.infrastructure.s3;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import tech.rket.shared.infrastructure.log.JDLogger;
import tech.rket.storage.domain.entity.StoredFileTemporaryUrl;
import tech.rket.storage.domain.value_object.StoredFileAuth;
import tech.rket.storage.domain.value_object.StoredFileAuthType;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Component
public class StorageS3Repository {
    private static final Logger log = JDLogger.getLogger(StorageS3Repository.class).build();
    private final MinioClient s3Client;
    private final String bucketNamePrefix;
    private final int validMinutes;

    public StorageS3Repository(StorageS3Config config) {
        bucketNamePrefix = config.getBucketName();
        validMinutes = config.getValidMinutes();

        s3Client = MinioClient.builder()
                .region(config.getRegion())
                .credentials(config.getAccessKey(), config.getSecretKey())
                .endpoint(config.getEndpoint())
                .build();
    }

    public void upload(StoredFileAuth owner, Long fileId, InputStream inputStream, long size, String contentType, Map<String, String> metadata) {
        String bucketName = ensureBucket(owner);
        try {
            PutObjectArgs request = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileId.toString())
                    .stream(inputStream, size, -1)
                    .userMetadata(metadata)
                    .contentType(contentType)
                    .build();
            log.info("Uploading original {} into bucket {}.", fileId, bucketName);
            s3Client.putObject(request);
            log.info("Uploaded original {} into bucket {} successfully.", fileId, bucketName);
        } catch (Exception e) {
            log.error("Upload original {} into bucket {} faced error due to {}.", fileId, bucketName, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public GetObjectResponse download(StoredFileAuth owner, Long file) {
        GetObjectArgs request = GetObjectArgs.builder()
                .bucket(getBucketName(owner))
                .object(file.toString())
                .build();
        try {
            return s3Client.getObject(request);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(StoredFileAuth owner, Long fileId) {
        String bucketName = ensureBucket(owner);
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileId.toString())
                .build();
        try {
            log.info("Deleting original {} from bucket {}.", fileId, bucketName);
            s3Client.removeObject(removeObjectArgs);
            log.info("Deleted original {} from bucket {} successfully.", fileId, bucketName);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Delete original {} from bucket {} faced error due to {}.", fileId, bucketName, e.getMessage(), e);
        }
    }

    public StoredFileTemporaryUrl sign(Long fileId, Long requestedTenantId, Long requestedUserId, StoredFileAuth owner) {
        String bucketName = getBucketName(owner);
        GetPresignedObjectUrlArgs.Builder request =
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileId.toString())
                        .method(Method.GET);
        Date expireDate = null;
        if (!owner.isPublic()) {
            expireDate = new Date(Instant.now().plus(validMinutes, ChronoUnit.MINUTES).toEpochMilli());
            request.extraHeaders(Map.of("x-tenant-id", requestedTenantId.toString(), "x-user-id", requestedUserId.toString()));
        } else {
            expireDate = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        }
        int expiry = (int) ((expireDate.getTime() - Instant.now().toEpochMilli()) / 1000);
        request.expiry(expiry);
        String presignedUrl = null;
        try {
            presignedUrl = s3Client.getPresignedObjectUrl(request.build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Delete original {} from bucket {} faced error due to {}.", fileId, bucketName, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return new StoredFileTemporaryUrl(fileId, owner.type(), expireDate, presignedUrl);
    }

    private String getBucketName(StoredFileAuth owner) {
        if (owner.type() == StoredFileAuthType.PUBLIC) {
            return String.format("%s-public", bucketNamePrefix);
        } else if (owner.type() == StoredFileAuthType.PRIVATE) {
            return String.format("%s-user-%d", bucketNamePrefix, owner.userId());
        } else {
            return String.format("%s-tenant-%d", bucketNamePrefix, owner.tenantId());
        }
    }

    public boolean hasBucket(StoredFileAuth owner) {
        String bucketName = getBucketName(owner);
        try {
            log.debug("Check bucket {} does exist.", bucketName);
            BucketExistsArgs args = BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build();
            boolean existence = s3Client.bucketExists(args);
            log.debug("Checking bucket {} does exist result is {}", bucketName, existence);
            return existence;
        } catch (Exception e) {
            log.error("Checking bucket {} faced error due to {}.", bucketName, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void createBucket(StoredFileAuth owner) {
        String bucketName = getBucketName(owner);
        try {
            log.debug("Creating bucket {}.", bucketName);
            MakeBucketArgs args = MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.makeBucket(args);
            log.debug("Created bucket {} successfully.", bucketName);
        } catch (Exception e) {
            log.error("Creation of bucket {} faced error due to {}.", bucketName, e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public String ensureBucket(StoredFileAuth owner) {
        if (!hasBucket(owner)) {
            createBucket(owner);
        }
        return getBucketName(owner);
    }
}
