package tech.rket.auth.application.workspace.info;

public record WorkspaceInfo(String identifier, String description, String name, boolean isDefault ,Long tenantId) {
}