package tech.rket.shared.infrastructure.restfilter.util;

import tech.rket.shared.infrastructure.restfilter.filter.Filter;
import tech.rket.shared.infrastructure.restfilter.filter.RangeFilter;
import tech.rket.shared.infrastructure.restfilter.filter.StringFilter;
import tech.rket.shared.infrastructure.restfilter.spring.filter.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class CriteriaToSpecificationConverter {
    private final QueryService<?> queryService;

    @SuppressWarnings("unchecked")
    public <T> Specification<T> convert(Object criteria) {
        if(criteria==null){
            return defaultSpec();
        }
        return (Specification<T>) getFilters(criteria.getClass())
                .stream()
                .map(method -> convert(criteria, method))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(defaultSpec(), Specification::and);
    }

    static <T> Specification<T> defaultSpec() {
        return Specification.where(null);
    }

    String getPropertyName(Method method) {
        String methodName = method.getName();

        if (methodName.startsWith("get") && methodName.length() > 3) {
            String propertyName = methodName.substring(3);
            if (propertyName.length() == 1) {
                return propertyName.toLowerCase();
            } else {
                return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
            }
        }

        throw new IllegalArgumentException("Method profile does not follow getter convention: " + methodName);
    }

    List<Method> getFilters(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("get"))
                .filter(method -> Filter.class.isAssignableFrom(method.getReturnType()))
                .filter(method -> method.getParameterCount() == 0)
                .toList();
    }

    <T> Optional<Specification<T>> convert(Object criteria, Method method) {
        String name = getPropertyName(method);
        try {
            Object object = method.invoke(criteria);
            if (object != null) {
                Specification<T> specification = null;
                if (object instanceof StringFilter stringFilter) {
                    specification = (Specification<T>) queryService.buildSpecification(stringFilter, r -> r.get(name));
                } else if (object instanceof RangeFilter<?> rangeFilter) {
                    specification = (Specification<T>) queryService.buildSpecification(rangeFilter, r -> r.get(name));
                } else if (object instanceof Filter<?> filter) {
                    specification = (Specification<T>) queryService.buildSpecification(filter, r -> r.get(name));
                }
                return Optional.ofNullable(specification);
            }
            return Optional.empty();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
