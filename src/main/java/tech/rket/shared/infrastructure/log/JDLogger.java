package tech.rket.shared.infrastructure.log;

import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.*;

public final class JDLogger implements Logger {
    @Getter(AccessLevel.PACKAGE)
    private final Logger logger;
    private final Map<String, Object> customFields = new LinkedHashMap<>();
    private static final List<JDLogBooter> STATIC_BOOTS = new ArrayList<>();
    private static final List<JDLogInterceptor> STATIC_INTERCEPTORS = new ArrayList<>();
    private final List<JDLogBooter> boots = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final List<JDLogInterceptor> interceptors = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final List<JDLogInterceptor> onceInterceptor = new ArrayList<>();
    @Getter(AccessLevel.PACKAGE)
    private final List<JDLogInterceptor> withoutOnceInterceptor = new ArrayList<>();
    @Getter
    private boolean build = false;


    public Map<String, Object> customFields() {
        return Collections.unmodifiableMap(customFields);
    }

    private JDLogger(Logger logger) {
        this.logger = logger;
        this.boots.addAll(STATIC_BOOTS);
        this.interceptors.addAll(STATIC_INTERCEPTORS);
    }

    public static void withStatic(JDLogBooter boot) {
        STATIC_BOOTS.add(boot);
    }

    public static void withStatic(JDLogInterceptor interceptor) {
        STATIC_INTERCEPTORS.add(interceptor);
    }


    public Logger withOnce(JDLogInterceptor interceptor) {
        checkBuild();
        onceInterceptor.add(interceptor);
        return this;
    }

    public Logger withoutOnce(JDLogInterceptor interceptor) {
        checkBuild();
        withoutOnceInterceptor.add(interceptor);
        return this;
    }

    public Logger with(JDLogInterceptor interceptor) {
        checkBeforeBuild();
        interceptors.add(interceptor);
        return this;
    }

    public Logger without(JDLogInterceptor interceptor) {
        checkBeforeBuild();
        interceptors.remove(interceptor);
        return this;
    }

    public Logger with(JDLogBooter boot) {
        checkBeforeBuild();
        boots.add(boot);
        return this;
    }

    public Logger without(JDLogBooter boot) {
        checkBeforeBuild();
        boots.remove(boot);
        return this;
    }


    public JDLogger copy() {
        JDLogger other = JDLogger.of(logger);
        other.customFields.putAll(customFields);
        other.boots.addAll(this.boots);
        other.build = false;
        return other;
    }

    public static JDLogger of(@Nonnull Logger logger) {
        return new JDLogger(logger);
    }

    public static JDLogger of(@Nonnull Logger logger, @Nonnull Map<String, Object> customFields) {
        JDLogger result = new JDLogger(logger);
        result.customFields.putAll(customFields);
        result.build();
        return result;
    }

    public static JDLogger getLogger(@Nonnull Class<?> clz, @Nonnull Map<String, Object> customFields) {
        return JDLogger.of(LoggerFactory.getLogger(clz), customFields);
    }

    public static JDLogger getLogger(@Nonnull String name, @Nonnull Map<String, Object> customFields) {
        return JDLogger.of(LoggerFactory.getLogger(name), customFields);
    }

    public static JDLogger getLogger(@Nonnull Class<?> clz) {
        return JDLogger.of(LoggerFactory.getLogger(clz));
    }

    public static JDLogger getLogger(@Nonnull String name) {
        return JDLogger.of(LoggerFactory.getLogger(name));
    }

    public JDLogger build() {
        checkBeforeBuild();
        build = true;
        boots.forEach(e -> e.boot(this));
        return this;
    }

    public JDLogger add(String key, String value) {
        if (build) {
            throw new IllegalStateException("Can not add custom values to built logger. use copy method to create new logger.");
        }
        customFields.put(key, value);
        return this;
    }

    public JDLogger addAll(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public LoggingEventBuilder makeLoggingEventBuilder(Level level) {
        return this.generateBuilder(level);
    }

    private LoggingEventBuilder generateBuilder(Level level) {
        checkBuild();
        return new JDLoggingEventBuilder(this, level);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {

        atTrace().log(s);

    }

    @Override
    public void trace(String s, Object o) {

        atTrace().log(s, o);

    }

    @Override
    public void trace(String s, Object o, Object o1) {

        atTrace().log(s, o, o1);

    }

    @Override
    public void trace(String s, Object... objects) {

        atTrace().log(s, objects);

    }

    @Override
    public void trace(String s, Throwable throwable) {

        atTrace().log(s, throwable);

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {

        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {

        generateBuilder(Level.TRACE).addMarker(marker).log(s);

    }

    @Override
    public void trace(Marker marker, String s, Object o) {

        generateBuilder(Level.TRACE).addMarker(marker).log(s, o);

    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {

        generateBuilder(Level.TRACE).addMarker(marker).log(s, o, o1);

    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {

        generateBuilder(Level.TRACE).addMarker(marker).log(s, objects);

    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {

        generateBuilder(Level.TRACE).addMarker(marker).log(s, throwable);

    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }


    @Override
    public void debug(String s) {

        atDebug().log(s);

    }

    @Override
    public void debug(String s, Object o) {

        atDebug().log(s, o);

    }

    @Override
    public void debug(String s, Object o, Object o1) {

        atDebug().log(s, o, o1);

    }

    @Override
    public void debug(String s, Object... objects) {

        atDebug().log(s, objects);

    }

    @Override
    public void debug(String s, Throwable throwable) {

        atDebug().log(s, throwable);

    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {

        generateBuilder(Level.DEBUG).addMarker(marker).log(s);

    }

    @Override
    public void debug(Marker marker, String s, Object o) {

        generateBuilder(Level.DEBUG).addMarker(marker).log(s, o);

    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {

        generateBuilder(Level.DEBUG).addMarker(marker).log(s, o, o1);

    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {

        generateBuilder(Level.DEBUG).addMarker(marker).log(s, objects);

    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {

        generateBuilder(Level.DEBUG).addMarker(marker).log(s, throwable);

    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }


    @Override
    public void info(String s) {

        atInfo().log(s);

    }

    @Override
    public void info(String s, Object o) {

        atInfo().log(s, o);

    }

    @Override
    public void info(String s, Object o, Object o1) {

        atInfo().log(s, o, o1);

    }

    @Override
    public void info(String s, Object... objects) {

        atInfo().log(s, objects);

    }

    @Override
    public void info(String s, Throwable throwable) {

        atInfo().log(s, throwable);

    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {

        generateBuilder(Level.INFO).addMarker(marker).log(s);

    }

    @Override
    public void info(Marker marker, String s, Object o) {

        generateBuilder(Level.INFO).addMarker(marker).log(s, o);

    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {

        generateBuilder(Level.INFO).addMarker(marker).log(s, o, o1);

    }

    @Override
    public void info(Marker marker, String s, Object... objects) {

        generateBuilder(Level.INFO).addMarker(marker).log(s, objects);

    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {

        generateBuilder(Level.INFO).addMarker(marker).log(s, throwable);

    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }


    @Override
    public void warn(String s) {

        atWarn().log(s);

    }

    @Override
    public void warn(String s, Object o) {

        atWarn().log(s, o);

    }

    @Override
    public void warn(String s, Object o, Object o1) {

        atWarn().log(s, o, o1);

    }

    @Override
    public void warn(String s, Object... objects) {

        atWarn().log(s, objects);

    }

    @Override
    public void warn(String s, Throwable throwable) {

        atWarn().log(s, throwable);

    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {

        generateBuilder(Level.WARN).addMarker(marker).log(s);

    }

    @Override
    public void warn(Marker marker, String s, Object o) {

        generateBuilder(Level.WARN).addMarker(marker).log(s, o);

    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {

        generateBuilder(Level.WARN).addMarker(marker).log(s, o, o1);

    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {

        generateBuilder(Level.WARN).addMarker(marker).log(s, objects);

    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {

        generateBuilder(Level.WARN).addMarker(marker).log(s, throwable);

    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String s) {

        atError().log(s);

    }

    @Override
    public void error(String s, Object o) {

        atError().log(s, o);

    }

    @Override
    public void error(String s, Object o, Object o1) {

        atError().log(s, o, o1);

    }

    @Override
    public void error(String s, Object... objects) {

        atError().log(s, objects);

    }

    @Override
    public void error(String s, Throwable throwable) {

        atError().log(s, throwable);

    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {

        atError().addMarker(marker).log(s);

    }

    @Override
    public void error(Marker marker, String s, Object o) {

        generateBuilder(Level.ERROR).addMarker(marker).log(s, o);

    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {

        generateBuilder(Level.ERROR).addMarker(marker).log(s, o, o1);

    }

    @Override
    public void error(Marker marker, String s, Object... objects) {

        generateBuilder(Level.ERROR).addMarker(marker).log(s, objects);

    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {

        generateBuilder(Level.ERROR).addMarker(marker).log(s, throwable);

    }

    private void checkBuild() {
        if (!build) {
            throw new IllegalStateException("Logger is not built already");
        }
    }

    private void checkBeforeBuild() {
        if (build) {
            throw new IllegalStateException("Logger is already built.");
        }
    }

    public void clearOnce() {
        getOnceInterceptor().clear();
        getWithoutOnceInterceptor().clear();
    }
}
