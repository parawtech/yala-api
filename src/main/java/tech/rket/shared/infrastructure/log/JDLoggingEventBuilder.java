package tech.rket.shared.infrastructure.log;

import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.spi.DefaultLoggingEventBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JDLoggingEventBuilder extends DefaultLoggingEventBuilder {
    private final List<KeyValuePair> pairs = new ArrayList<>();
    private final JDLogger jdLogger;

    public JDLoggingEventBuilder(JDLogger logger, Level level) {
        super(logger.getLogger(), level);
        jdLogger = logger;
        for (Map.Entry<String, Object> entry : jdLogger.customFields().entrySet()) {
            pairs.add(new KeyValuePair(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    protected void log(LoggingEvent aLoggingEvent) {
        DefaultLoggingEvent defaultLoggingEvent = (DefaultLoggingEvent) aLoggingEvent;
        pairs.forEach(e -> defaultLoggingEvent.addKeyValue(e.key, e.value));
        before(aLoggingEvent);
        super.log(aLoggingEvent);
        after(aLoggingEvent);
    }

    private void before(LoggingEvent aLoggingEvent) {
        jdLogger.getInterceptors().stream()
                .filter(e -> !jdLogger.getWithoutOnceInterceptor().contains(e))
                .forEach(e -> e.before(jdLogger, aLoggingEvent));
        jdLogger.getOnceInterceptor()
                .forEach(e -> e.before(jdLogger, aLoggingEvent));
    }

    private void after(LoggingEvent aLoggingEvent) {
        jdLogger.getInterceptors().stream()
                .filter(e -> !jdLogger.getWithoutOnceInterceptor().contains(e))
                .forEach(e -> e.after(jdLogger, aLoggingEvent));
        jdLogger.clearOnce();
    }
}
