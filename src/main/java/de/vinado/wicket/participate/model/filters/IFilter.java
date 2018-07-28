package de.vinado.wicket.participate.model.filters;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface IFilter<T> {

    List<T> filter(List<T> list);

    boolean validate(String str1, String str2);
}
