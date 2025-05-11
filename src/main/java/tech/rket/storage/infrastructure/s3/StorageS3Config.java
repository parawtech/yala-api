package tech.rket.storage.infrastructure.s3;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties("storage.s3")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class StorageS3Config {
    private String region;
    private String bucketName;
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private int poolSize = 10;
    private int validMinutes = 60;
}
