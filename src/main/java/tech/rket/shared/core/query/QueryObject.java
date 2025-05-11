package tech.rket.shared.core.query;

import tech.rket.shared.core.shared.HasId;

public interface QueryObject<QUERY_OBJECT_ID> extends HasId<QUERY_OBJECT_ID> {
    interface Record<QUERY_OBJECT_ID> extends QueryObject<QUERY_OBJECT_ID>, HasId.Record<QUERY_OBJECT_ID> {
    }
}

