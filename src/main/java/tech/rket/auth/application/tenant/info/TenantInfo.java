package tech.rket.auth.application.tenant.info;

import com.fasterxml.jackson.annotation.JsonFormat;

public record TenantInfo(
        @JsonFormat(shape = JsonFormat.Shape.STRING) Long id,
        String name) {
}
