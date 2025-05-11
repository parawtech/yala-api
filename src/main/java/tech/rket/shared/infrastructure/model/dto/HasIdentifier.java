package tech.rket.shared.infrastructure.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public interface HasIdentifier {
    String IDENTIFIER_REGEX_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9-]{1,34}[a-zA-Z0-9]$";

    @NotNull
    @Pattern(regexp = IDENTIFIER_REGEX_PATTERN)
    @Schema(pattern = IDENTIFIER_REGEX_PATTERN, example = "identifier-1")
    String getIdentifier();

    void setIdentifier(String identifier);
}
