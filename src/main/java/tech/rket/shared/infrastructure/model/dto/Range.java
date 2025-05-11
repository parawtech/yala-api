package tech.rket.shared.infrastructure.model.dto;

import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.util.function.Supplier;

public interface Range<T> extends Serializable {
    <E extends T> E getMin();

    <E extends T> E getMax();

    default boolean isValid(T object) {
        if (this.getMin() != null && compare(this.getMin(), object) > 0) {
            return false;
        } else {
            return this.getMax() == null || compare(this.getMax(), object) >= 0;
        }
    }

    default int compare(T t, T other) {
        if (t == null && other == null) return 0;
        if (t == null) return -1;
        if (other == null) return 1;
        IllegalStateException err = new IllegalStateException(String.format("Should be Comparable<%s> or reimplement.", t.getClass().getName()));
        if (t instanceof Comparable tComparable) {
            try {
                return tComparable.compareTo(other);
            } catch (ClassCastException e) {
                throw err;
            }
        }
        throw err;
    }

    default Object min() {
        return this.getMin() == null ? "∞" : this.getMin();
    }

    default Object max() {
        return this.getMax() == null ? "∞" : this.getMax();
    }


    static <E> void check(Range<E> range, E value, @Nonnull Supplier<RuntimeException> e) {
        if (range != null && value != null && !range.isValid(value)) {
            throw e.get();
        }
    }
}
