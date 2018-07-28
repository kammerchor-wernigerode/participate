package de.vinado.wicket.participate.model.filters;

import org.apache.wicket.ajax.AjaxRequestTarget;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface IFilterPanel<F> {

    void onSearch(AjaxRequestTarget target, F filter);

    void onReset(AjaxRequestTarget target);
}
