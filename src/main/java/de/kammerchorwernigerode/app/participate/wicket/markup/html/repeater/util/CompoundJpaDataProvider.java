package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

import lombok.Getter;
import lombok.NonNull;

public abstract class CompoundJpaDataProvider<T extends Serializable, F extends Specification<T>>
    extends JpaDataProvider<T, String[], F> {

    @Getter
    private final SortState sortState = new SortState();

    public CompoundJpaDataProvider(JpaSpecificationExecutor<T> jpaSpecificationExecutor,
                                   IModel<F> filterState) {
        super(jpaSpecificationExecutor, filterState);
    }

    @Override
    public Sort getSort() {
        return sortState.getSort();
    }

    public void setSort(@NonNull Sort sort) {
        sortState.setSort(sort);
    }

    public void setSort(@NonNull String[] properties, @NonNull SortOrder sortOrder) {
        sortState.setPropertySortOrder(properties, sortOrder);
    }
}
