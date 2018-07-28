package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.data.filter.IFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;

import java.util.Collection;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class FilterableDateProvider<T, S, R extends IFilter<T>> extends SimpleDataProvider<T, S> implements IFilterStateLocator<R> {

    // TODO Data provider ignores rows with NULL properties. Fix this

    private R filter;

    public FilterableDateProvider(final R filter) {
        super();
        this.filter = filter;
    }

    public FilterableDateProvider(final Collection<T> data, final R filter) {
        super(data);
        this.filter = filter;
    }

    public FilterableDateProvider(final Collection<T> data, final R filter, final boolean asc) {
        super(data, asc);
        this.filter = filter;
    }

    @Override
    public R getFilterState() {
        return filter;
    }

    @Override
    public void setFilterState(final R state) {
        this.filter = state;
    }

    @Override
    public long size() {
        return filter.filter(data).size();
    }

    @Override
    protected List<T> filter(final List<T> list) {
        return filter.filter(super.filter(list));
    }
}
