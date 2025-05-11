package tech.rket.shared.infrastructure.apm;

import tech.rket.shared.infrastructure.log.JDLogger;

public class ApmLoggerInitializer {
    public static void init() {
        JDLogger.withStatic(SpanIdLogInterceptor.INSTANCE);
        JDLogger.withStatic(ApmLablerLogInterceptor.INSTANCE);
    }
}
