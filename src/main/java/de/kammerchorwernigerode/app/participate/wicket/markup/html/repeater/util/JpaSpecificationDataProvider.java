package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import org.apache.wicket.model.IModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor.SpecificationFluentQuery;

import java.io.Serializable;

public class JpaSpecificationDataProvider<T extends Serializable, S extends Serializable, F extends Specification<T>>
    extends AbstractJpaSpecificationDataProvider<T, T, S, F> {

    public JpaSpecificationDataProvider(IModel<F> spec, SpringSortState<S> sortState,
                                        JpaSpecificationExecutor<T> jpaSpecificationExecutor) {
        super(spec, sortState, jpaSpecificationExecutor);
    }

    @Override
    protected SpecificationFluentQuery<T> project(SpecificationFluentQuery<T> query) {
        return query;
    }
}
