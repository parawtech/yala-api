package tech.rket.shared.core.domain.result;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DomainResult<T> {
    static <T> DomainResult<T> dependsOn(@Nullable Collection<DomainConstraintViolation> violations, Supplier<@NonNull T> valueSupplier) {
        return violations == null || violations.isEmpty() ? success(valueSupplier.get()) : fail(violations.toArray(new DomainConstraintViolation[0]));
    }

    static <T> DomainResult<T> fail(List<DomainConstraintViolation> errors) {
        return new DomainResultImpl<>(null, false, errors);
    }

    static <T> DomainResult<T> fail(DomainConstraintViolation... error) {
        return new DomainResultImpl<>(null, false, new ArrayList<>(List.of(error)));
    }

    static <T> DomainResult<T> success(T value) {
        return new DomainResultImpl<>(value, true, List.of());
    }

    static <T> DomainResult<T> success() {
        return new DomainResultImpl<>(null, true, List.of());
    }


    boolean isSuccess();

    boolean isFailure();

    @Nonnull
    List<DomainConstraintViolation> violations();

    T value();

    default <X extends Throwable> DomainResult<T> throwIfFailure(Function<List<DomainConstraintViolation>, ? extends X> exceptionSupplier) throws X {
        if (this.isSuccess()) {
            return this;
        } else {
            throw (X) exceptionSupplier.apply(this.violations());
        }
    }

    default <E> DomainResult<E> merge(DomainResult<E> domainResult) {
        return new DomainResultImpl<>(domainResult.value(), domainResult.isSuccess(), domainResult.violations());
    }

    default DomainResult<T> peek(Consumer<T> consumer) {
        if (isSuccess()) {
            consumer.accept(value());
        }
        return this;
    }

    default <E> DomainResult<E> map(Function<T, E> function) {
        if (isFailure()) {
            return new DomainResultImpl<>(null, false, violations());
        } else {
            return success(function.apply(value()));
        }
    }

    @ToString
    final class DomainResultImpl<T> implements DomainResult<T> {

        private final boolean isSuccess;
        private final List<DomainConstraintViolation> errors;
        private final T value;

        DomainResultImpl(T value, boolean isSuccess, List<DomainConstraintViolation> errors) {
            if (isSuccess && !errors.isEmpty())
                throw new IllegalStateException();
            if (!isSuccess && errors.isEmpty())
                throw new IllegalStateException();

            this.isSuccess = isSuccess;
            this.errors = errors;
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return isSuccess;
        }

        @Override
        public boolean isFailure() {
            return !isSuccess;
        }

        @Override
        public T value() {
            return value;
        }

        @Override
        public List<DomainConstraintViolation> violations() {
            return Collections.unmodifiableList(errors);
        }
    }
}
