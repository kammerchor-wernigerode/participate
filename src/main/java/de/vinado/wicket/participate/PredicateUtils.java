package de.vinado.wicket.participate;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

/**
 * Collection of convenience methods related to {@link Predicate predicates}.
 *
 * @author Vincent Nadoll
 */
public final class PredicateUtils {

    /**
     * Negates the given predicate.
     *
     * @param predicate must not be {@code null}
     * @param <T>       type of the predicate
     * @return negated predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    /**
     * Tests if the given string equals the predicate.
     *
     * @param test must not be {@code null}
     * @return predicate indicates whether the given string equals the predicate ones
     */
    public static Predicate<String> equalsString(String test) {
        return o -> StringUtils.equals(o, test);
    }
}
