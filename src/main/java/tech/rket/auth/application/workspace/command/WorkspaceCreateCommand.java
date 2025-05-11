package tech.rket.auth.application.workspace.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record WorkspaceCreateCommand(@NotBlank @Size(min = 3, max = 200) String name,
                                     String description,
                                     @NotNull
                                     @Pattern(regexp = IDENTIFIER_REGEX_PATTERN)
                                     @Schema(description = IDENTIFIER_REGEX_PATTERN, example = "URL ENCODED")
                                     String identifier) {
    private static final String IDENTIFIER_REGEX_PATTERN = "^[a-zA-Z0-9][a-zA-Z0-9-_.~%]{0,253}[a-zA-Z0-9]$";

}