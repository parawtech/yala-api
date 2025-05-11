package tech.rket.shared.infrastructure.persistence.query;

import tech.rket.shared.core.query.QueryObject;
import tech.rket.shared.infrastructure.persistence.PersistedObject;

public abstract class SameIdSharedQueryRepository<
        PERSISTED_OBJECT extends PersistedObject<ID>,
        QUERY_OBJECT extends QueryObject<ID>,
        ID>
        extends SharedQueryRepository<PERSISTED_OBJECT, ID, QUERY_OBJECT, ID> {

    @Override
    protected ID convertID(ID val) {
        return val;
    }
}
