package de.vinado.wicket.participate.common;

import java.util.Objects;

@FunctionalInterface
public interface Sorted {

    int getSortOrder();

    static <T extends Sorted> Comparator<T> compare() {
        return new Comparator<>();
    }


    final class Comparator<T extends Sorted> implements java.util.Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return Objects.compare(o1.getSortOrder(), o2.getSortOrder(), Integer::compareTo);
        }
    }
}
