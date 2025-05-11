package tech.rket.shared.infrastructure.apm;

import co.elastic.apm.api.ElasticApm;
import tech.rket.shared.infrastructure.log.JDLogInterceptor;
import tech.rket.shared.infrastructure.log.JDLogger;
import org.slf4j.MDC;
import org.slf4j.event.LoggingEvent;

public final class SpanIdLogInterceptor implements JDLogInterceptor {
    public static final SpanIdLogInterceptor INSTANCE = new SpanIdLogInterceptor();

    private SpanIdLogInterceptor() {
    }

    public void before(JDLogger logger, LoggingEvent aLoggingEvent) {
        MDC.put("span.id", ElasticApm.currentSpan().getId());
    }

    public void after(JDLogger logger, LoggingEvent aLoggingEvent) {
        MDC.remove("span.id");
    }
}
