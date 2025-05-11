package tech.rket.shared.infrastructure.apm;

public final class ApmTracedStatic {
    private ApmTracedStatic() {
    }
    public static final String SEPARATOR = ",";
    //Types
    public static final String TYPE_HTTP = "http";
    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_EXTERNAL = "ext";
    public static final String TYPE_TEMPLATE = "template";
    public static final String TYPE_CACHE = "cache";
    public static final String TYPE_DB = "db";
    // Subtypes
    public static final String SUBTYPE_SPRING_ = "spring,";
    public static final String SUBTYPE_JPA_ = "jpa.properties,";
    public static final String SUBTYPE_REPOSITORY_ = "repository,";
    //Actions
    public static final String ACTION_REQUEST_ = "request,";
    public static final String ACTION_PROCESS_ = "process,";
    public static final String ACTION_VALIDATE_ = "validate,";
    public static final String ACTION_CALCULATE_ = "calculate,";
    public static final String ACTION_TRANSFORM = "transform";
    public static final String ACTION_ASSEMBLE = ACTION_TRANSFORM + ",assemble";
    public static final String ACTION_QUERY_ = "query,";
    // Words
    public static final String MYSQL = "mysql";
    public static final String SERVICE = "service";
    public static final String REST = "rest";
    public static final String CREATE = "create";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String PATCH = "patch";
    public static final String GET = "get";
    public static final String SEARCH = "search";

    public static final String HEAD = "head";
    public static final String AUTH = "auth";
    public static final String LIFE_CYCLE = "lifecycle";
    public static final String PERSIST = "persist";
    public static final String UPSERT = "upsert";
    public static final String COUNT = "count";
    public static final String EXISTS = "exists";
    // Suffix
    public static final String _METADATA = ",metadata";
    public static final String _PARTIALLY = ",partially";
    public static final String _SUBRESOURCE = ",subresource";
    public static final String _RELATED = ",related-resource";
    public static final String _SINGLE = ",single";
    public static final String _LIST = ",list";
}


