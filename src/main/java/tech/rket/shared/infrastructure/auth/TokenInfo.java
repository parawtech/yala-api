package tech.rket.shared.infrastructure.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class TokenInfo {
    @JsonProperty("iat")
    Instant issuedAt;
    @JsonProperty("exp")
    Instant expiresAt;
    @JsonProperty("sub")
    Long subject;
    @JsonProperty("jti")
    UUID jwtId;
    @JsonProperty("tenant.id")
    Long tenantId;
    @JsonProperty("user.email")
    String email;
    @JsonProperty("role.id")
    String role;
    @JsonProperty(value = "permissions")
    Set<String> permissions;
    @JsonProperty(value = "user.locale")
    String locale;

    public void setPermissions(Set<Map<Object, Object>> permissions) {
        this.permissions = permissions.stream().map(permission -> (String) permission.get("id")).collect(Collectors.toSet());
    }
}
