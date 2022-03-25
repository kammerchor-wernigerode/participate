package de.vinado.util;

import org.danekja.java.util.function.serializable.SerializablePredicate;

import java.util.Objects;

/**
 * @author Vincent Nadoll
 */
public final class SerializablePredicates {

    private SerializablePredicates() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public static <T> SerializablePredicate<T> not(SerializablePredicate<? extends T> target) {
        Objects.requireNonNull(target);
        return (SerializablePredicate<T>) target.negate();
    }
}
