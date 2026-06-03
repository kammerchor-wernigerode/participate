package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor.SpecificationFluentQuery;
import org.springframework.data.util.Streamable;

import java.io.Serializable;

// @checkstyle:off: LineLength
public abstract class AbstractJpaSpecificationDataProvider<T extends Serializable, E, S extends Serializable, F extends Specification<E>>
    extends SortableSpringDataProvider<T, S> implements IFilterStateLocator<F> {
    // @checkstyle:on: LineLength

    private final IModel<F> spec;
    private final JpaSpecificationExecutor<E> jpaSpecificationExecutor;

    public AbstractJpaSpecificationDataProvider(IModel<F> spec, SpringSortState<S> sortState,
                                                JpaSpecificationExecutor<E> jpaSpecificationExecutor) {
        super(sortState);
        this.spec = spec;
        this.jpaSpecificationExecutor = jpaSpecificationExecutor;
    }

    @Override
    public Streamable<T> streamable(Pageable pageable) {
        F spec = getFilterState();
        return streamable(spec, pageable);
    }

    public Streamable<T> streamable(F spec, Pageable pageable) {
        return jpaSpecificationExecutor.findBy(spec, query -> project(query)
            .slice(pageable));
    }

    protected abstract SpecificationFluentQuery<T> project(SpecificationFluentQuery<E> query);

    @Override
    public long size() {
        F spec = getFilterState();
        return size(spec);
    }

    public long size(F spec) {
        return jpaSpecificationExecutor.count(spec);
    }

    @Override
    public F getFilterState() {
        return spec.getObject();
    }

    @Override
    public void setFilterState(F state) {
        spec.setObject(state);
    }

    @Override
    public void detach() {
        spec.detach();
        super.detach();
    }
}
