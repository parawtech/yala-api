package tech.rket.shared.infrastructure.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

public interface HasName {
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @NoArgsConstructor
    final class Impl implements HasName {
        @NotNull
        private String name;
        private String description;
    }
}