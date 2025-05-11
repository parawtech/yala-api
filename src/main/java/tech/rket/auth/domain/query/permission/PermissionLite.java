package tech.rket.auth.domain.query.permission;

import tech.rket.shared.core.query.QueryObject;

public record PermissionLite(String id, String name) implements QueryObject.Record<String> {
}
