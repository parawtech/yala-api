package tech.rket.shared.infrastructure.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;

import java.io.Serializable;

public interface HasId<E extends Serializable & Comparable<E>> extends Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, type = "string")
    @Null
    E getId();

    void setId(E id);
}