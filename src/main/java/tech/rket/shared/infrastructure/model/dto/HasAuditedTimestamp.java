package tech.rket.shared.infrastructure.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;

import java.io.Serializable;
import java.time.Instant;

public interface HasAuditedTimestamp extends Serializable {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    Instant getCreatedDate();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Null
    Instant getUpdatedDate();

    void setCreatedDate(Instant createdDate);

    void setUpdatedDate(Instant updatedDate);
}