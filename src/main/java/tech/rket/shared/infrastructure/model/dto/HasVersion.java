package tech.rket.shared.infrastructure.model.dto;

import jakarta.validation.constraints.Null;

import java.io.Serializable;

public interface HasVersion extends Serializable {
    @Null
    Integer getVersion();

    void setVersion(Integer name);
}