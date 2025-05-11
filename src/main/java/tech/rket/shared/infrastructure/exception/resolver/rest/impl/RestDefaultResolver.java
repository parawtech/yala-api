package tech.rket.shared.infrastructure.exception.resolver.rest.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tech.rket.shared.infrastructure.exception.ExceptionNameConverter;
import tech.rket.shared.infrastructure.exception.NamedParameterized;
import tech.rket.shared.infrastructure.exception.Parameterized;
import tech.rket.shared.infrastructure.exception.Problem;
import tech.rket.shared.infrastructure.exception.models.RestErrorMessage;
import tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver;
import tech.rket.shared.infrastructure.object_mapper.ObjectMapperGenerator;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class RestDefaultResolver implements RestExceptionResolver<Throwable> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperGenerator.jsonMapper();

    @Nullable
    @Override
    public RestErrorMessage resolve(@Nonnull Throwable ex, @Nonnull WebRequest request) {
        FoundAnnotation<Problem> problem = getProblem(ex.getClass());
        if (problem != null) {
            handleProblemMetadataCookies(ex, problem.annotation.metadata(), ((ServletWebRequest) request).getResponse());
        }
        FoundAnnotation<ResponseStatus> responseStatus = getResponseStatus(ex.getClass());
        return create(problem, responseStatus, ex);
    }

    private void handleProblemMetadataCookies(Throwable ex, Problem.MetaData[] metaData, HttpServletResponse response) {
        Arrays.asList(metaData).forEach(md -> {
            if (md.as().equals(Problem.METADATA_COOKIE)) {
                String name = md.name().isBlank() ? md.field() : md.name();
                Object object = getObject(ex, md.field());
                if (object != null) {
                    try {
                        response.addCookie(new Cookie(name, OBJECT_MAPPER.writeValueAsString(object)));
                    } catch (JsonProcessingException e) {
                        response.addCookie(new Cookie(name, object.toString()));
                    }
                }
            }
        });
    }

    private Object getObject(Throwable ex, String field) {
        try {
            Field f = ex.getClass().getField(field);
            f.setAccessible(true);
            return f.get(ex);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            return null;
        }
    }

    private RestErrorMessage create(FoundAnnotation<Problem> problem, FoundAnnotation<ResponseStatus> responseStatus, @Nonnull Throwable ex) {
        String code = createCode(problem, ex);

        Integer status = createStatus(problem, responseStatus);

        RestErrorMessage response = RestErrorMessage.builder()
                .code(code)
                .namedArguments(ex instanceof NamedParameterized)
                .status(status)
                .build();

        if (problem != null) {
            Problem.Utils.handleRoot(problem.annotation.root(), response.getRoot(), ex);
            Problem.Utils.handleDetail(problem.annotation.details(), response.getDetails(), ex);

            Arrays.stream(problem.annotation.metadata())
                    .filter(e -> e.as().equals(Problem.METADATA_HEADER))
                    .forEach(md -> {
                        String name = md.name().isBlank() ? md.field() : md.name();
                        Object object = getObject(ex, md.field());
                        try {
                            response.getHeaders().add(name, OBJECT_MAPPER.writeValueAsString(object));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

            Stream.of("cause", "suppressed", "message", "depth", "localizedMessage", "stackTrace")
                    .forEach(s -> {
                        response.getRoot().remove(s);
                    });
        }
        if (ex instanceof Parameterized exp) {
            int i = 0;
            for (Object o : exp.getParameters()) {
                response.getParameters().put("" + i++, o);
            }
        } else if (ex instanceof NamedParameterized exnp) {
            response.getParameters().putAll(exnp.getParameters());
        }
        return response;
    }

    private String createCode(FoundAnnotation<Problem> problem, Throwable ex) {
        String code = null;
        if (problem != null && problem.level == 0) {
            code = problem.annotation.code();
        }
        if (code == null) {
            code = ExceptionNameConverter.convert(ex.getClass());
        }
        return code;
    }

    private Integer createStatus(FoundAnnotation<Problem> problem, FoundAnnotation<ResponseStatus> responseStatus) {
        Integer status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (problem == null) {
            if (responseStatus != null) {
                status = responseStatus.annotation.value().value();
            }
        } else {
            if ((responseStatus == null || responseStatus.level >= problem.level) && !problem.annotation.status().isBlank()) {
                status = status(problem.annotation.status());
            } else if (responseStatus != null) {
                status = responseStatus.annotation.value().value();
            }
        }
        return status;
    }

    private Integer status(String status) {
        int value = HttpStatus.INTERNAL_SERVER_ERROR.value();
        try {
            value = Integer.parseInt(status);
        } catch (NumberFormatException e) {
            try {
                value = HttpStatus.valueOf(status.toUpperCase()).value();
            } catch (IllegalArgumentException ignored) {
            }
        }
        return value;
    }

    private FoundAnnotation<Problem> getProblem(Class<? extends Throwable> aClass) {
        Problem problem;
        int level = -1;
        Class<?> clz = aClass;
        do {
            problem = getRestProblem(clz);
            clz = clz.getSuperclass();
            level++;
        } while (clz != null && problem == null);
        return problem == null ? null : new FoundAnnotation<>(problem, level);
    }

    private Problem getRestProblem(Class<?> clz) {
        Problem.ParamList paramList = clz.getAnnotation(Problem.ParamList.class);
        Problem problem = clz.getAnnotation(Problem.class);
        if (problem == null || !problem.protocol().equals(protocol())) {
            if (paramList != null) {
                problem = Arrays.stream(paramList.value()).filter(s -> s.protocol().equals(Problem.PROTOCOL_REST)).findAny().orElse(null);
            } else {
                problem = null;
            }
        }
        return problem;
    }

    private FoundAnnotation<ResponseStatus> getResponseStatus(Class<? extends Throwable> aClass) {
        ResponseStatus status;
        int level = -1;
        Class<?> clz = aClass;
        do {
            status = clz.getAnnotation(ResponseStatus.class);
            clz = clz.getSuperclass();
            level++;
        } while (clz != null && status == null);
        return status == null ? null : new FoundAnnotation<>(status, level);
    }

    @Override
    public Class<Throwable> supportedClass() {
        return Throwable.class;
    }

    @AllArgsConstructor
    static class FoundAnnotation<T extends Annotation> {
        private final T annotation;
        private final int level;
    }
}


