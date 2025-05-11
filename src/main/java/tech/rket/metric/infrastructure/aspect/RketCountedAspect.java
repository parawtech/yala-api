package tech.rket.metric.infrastructure.aspect;

import io.micrometer.common.lang.NonNullApi;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;

@Aspect
@NonNullApi
public class RketCountedAspect {

    private static final Predicate<ProceedingJoinPoint> DONT_SKIP_ANYTHING = pjp -> false;

    public final String DEFAULT_EXCEPTION_TAG_VALUE = "none";

    public final String RESULT_TAG_FAILURE_VALUE = "failure";

    public final String RESULT_TAG_SUCCESS_VALUE = "success";

    private static final String RESULT_TAG = "result";
    private RketCountedMeterTagAnnotationHandler meterTagAnnotationHandler;

    private static final String EXCEPTION_TAG = "exception";
    private final MeterRegistry registry;
    private final Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    private final Predicate<ProceedingJoinPoint> shouldSkip;

    public RketCountedAspect() {
        this(Metrics.globalRegistry);
    }

    public RketCountedAspect(MeterRegistry registry) {
        this(registry, DONT_SKIP_ANYTHING);
    }

    public RketCountedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this(registry, tagsBasedOnJoinPoint, DONT_SKIP_ANYTHING);
    }

    public RketCountedAspect(MeterRegistry registry, Predicate<ProceedingJoinPoint> shouldSkip) {
        this(registry, pjp -> Tags.of("class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
                pjp.getStaticPart().getSignature().getName()), shouldSkip);
    }

    public RketCountedAspect(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint,
                             Predicate<ProceedingJoinPoint> shouldSkip) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
        this.shouldSkip = shouldSkip;
    }

    @Around(value = "@annotation(counted)", argNames = "pjp,counted")
    public Object interceptAndRecord(ProceedingJoinPoint pjp, Counted counted) throws Throwable {
        if (shouldSkip.test(pjp)) {
            return pjp.proceed();
        }

        final Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        final boolean stopWhenCompleted = CompletionStage.class.isAssignableFrom(method.getReturnType());

        if (stopWhenCompleted) {
            try {
                return ((CompletionStage<?>) pjp.proceed())
                        .whenComplete((result, throwable) -> recordCompletionResult(pjp, counted, throwable));
            } catch (Throwable e) {
                record(pjp, counted, e.getClass().getSimpleName(), RESULT_TAG_FAILURE_VALUE);
                throw e;
            }
        }

        try {
            Object result = pjp.proceed();
            if (!counted.recordFailuresOnly()) {
                record(pjp, counted, DEFAULT_EXCEPTION_TAG_VALUE, RESULT_TAG_SUCCESS_VALUE);
            }
            return result;
        } catch (Throwable e) {
            record(pjp, counted, e.getClass().getSimpleName(), RESULT_TAG_FAILURE_VALUE);
            throw e;
        }
    }

    private void recordCompletionResult(ProceedingJoinPoint pjp, Counted counted, Throwable throwable) {

        if (throwable != null) {
            String exceptionTagValue = throwable.getCause() == null ? throwable.getClass().getSimpleName()
                    : throwable.getCause().getClass().getSimpleName();
            record(pjp, counted, exceptionTagValue, RESULT_TAG_FAILURE_VALUE);
        } else if (!counted.recordFailuresOnly()) {
            record(pjp, counted, DEFAULT_EXCEPTION_TAG_VALUE, RESULT_TAG_SUCCESS_VALUE);
        }

    }

    private void record(ProceedingJoinPoint pjp, Counted counted, String exception, String result) {
        Counter.Builder builder = counter(pjp, counted).tag(EXCEPTION_TAG, exception)
                .tag(RESULT_TAG, result)
                .tags(counted.extraTags());
        if (meterTagAnnotationHandler != null) {
            meterTagAnnotationHandler.addAnnotatedParameters(builder, pjp);
        }
        builder.register(registry).increment();
    }

    private Counter.Builder counter(ProceedingJoinPoint pjp, Counted counted) {
        Counter.Builder builder = Counter.builder(counted.value()).tags(tagsBasedOnJoinPoint.apply(pjp));
        String description = counted.description();
        if (!description.isEmpty()) {
            builder.description(description);
        }
        return builder;
    }

    public void setMeterTagAnnotationHandler(RketCountedMeterTagAnnotationHandler meterTagAnnotationHandler) {
        this.meterTagAnnotationHandler = meterTagAnnotationHandler;
    }

}
