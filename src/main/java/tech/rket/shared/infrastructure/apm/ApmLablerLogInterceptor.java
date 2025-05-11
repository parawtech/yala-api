package tech.rket.shared.infrastructure.apm;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import tech.rket.shared.infrastructure.log.JDLogInterceptor;
import tech.rket.shared.infrastructure.log.JDLogger;
import org.slf4j.event.LoggingEvent;

import java.util.Map;

public final class ApmLablerLogInterceptor implements JDLogInterceptor {
    public static final ApmLablerLogInterceptor INSTANCE = new ApmLablerLogInterceptor();

    private static ThreadLocal<Boolean> hasDone;

    private ApmLablerLogInterceptor() {
    }

    public void before(JDLogger logger, LoggingEvent aLoggingEvent) {
        if (!isSet()) {
            Span span = ElasticApm.currentSpan();
            for (Map.Entry<String, Object> entry : logger.customFields().entrySet()) {
                span.setLabel(entry.getKey(), (String) entry.getValue());
            }
            set();
        }
    }

    public void after(JDLogger logger, LoggingEvent aLoggingEvent) {
    }

    private boolean isSet() {
        return hasDone != null && hasDone.get() != null && hasDone.get();
    }

    private void set() {
        hasDone = new ThreadLocal<>();
        hasDone.set(true);
    }
}
