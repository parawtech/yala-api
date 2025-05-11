package tech.rket.shared.infrastructure.js;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class LazyExecutorFactory extends BasePooledObjectFactory<NashronJsExecutor> {
    @Override
    public NashronJsExecutor create() {
        return new NashronJsExecutor();
    }

    @Override
    public PooledObject<NashronJsExecutor> wrap(NashronJsExecutor executor) {
        return new DefaultPooledObject<>(executor);
    }

    @Override
    public void passivateObject(PooledObject<NashronJsExecutor> pooled) {
        pooled.getObject().clearContext();
    }
}