package tech.rket.shared.infrastructure.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.LinkedHashMap;

public interface JDAuditedEntity {
    @JsonIgnore
    @Schema(hidden = true)
    LinkedHashMap<Number, Instant> getAuditRevisions();

    @JsonIgnore
    @Schema(hidden = true)
    void setAuditRevisions(LinkedHashMap<Number, Instant> revisions);
}
