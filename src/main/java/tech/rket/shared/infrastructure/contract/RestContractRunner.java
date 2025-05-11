package tech.rket.shared.infrastructure.contract;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import tech.rket.shared.contract.ContractException;
import tech.rket.shared.infrastructure.exception.ExceptionNameConverter;
import tech.rket.shared.infrastructure.exception.Problem;

import java.util.Arrays;
import java.util.function.Supplier;

import static tech.rket.shared.infrastructure.exception.resolver.rest.RestExceptionResolver.MANAGED_EXCEPTIONS;

@Component
public class RestContractRunner {
    public <T> T runDirect(Supplier<T> runner) {
        try {
            return runner.get();
        } catch (Exception e) {
            throw new ContractException(findExceptionCode(e), 500, e);
        }
    }

    public <T> T run(Supplier<ResponseEntity<T>> runner) {
        try {
            return runner.get().getBody();
        } catch (Exception e) {
            throw new ContractException(findExceptionCode(e), 500, e);
        }
    }

    private String problemCode(Class<? extends Throwable> aClass) {
        Problem problem;
        Class<?> clz = aClass;
        do {
            problem = getRestProblem(clz);
            clz = clz.getSuperclass();
        } while (clz != null && problem == null);
        return problem == null ? null : problem.code();
    }

    private Problem getRestProblem(Class<?> clz) {
        Problem.ParamList paramList = clz.getAnnotation(Problem.ParamList.class);
        Problem problem = clz.getAnnotation(Problem.class);
        if (problem == null || !problem.protocol().equals("rest")) {
            if (paramList != null) {
                problem = Arrays.stream(paramList.value()).filter(s -> s.protocol().equals(Problem.PROTOCOL_REST)).findAny().orElse(null);
            } else {
                problem = null;
            }
        }
        return problem;
    }

    public String findExceptionCode(Throwable e) {
        String code = problemCode(e.getClass());
        if (code == null) {
            if (MANAGED_EXCEPTIONS.contains(e.getClass()) || MANAGED_EXCEPTIONS.stream().anyMatch(ex -> ex.isAssignableFrom(e.getClass()))) {
                return e.getClass().getSimpleName();
            } else {
                return ExceptionNameConverter.convert(e.getClass());
            }
        }
        return code;
    }
}
