package tech.rket.shared.infrastructure.log;

import org.slf4j.event.LoggingEvent;

public interface JDLogInterceptor {
    void before(JDLogger logger, LoggingEvent event);

    void after(JDLogger logger, LoggingEvent event);
}
