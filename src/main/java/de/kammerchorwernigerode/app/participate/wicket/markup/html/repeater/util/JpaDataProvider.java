package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.data.domain.OffsetPageRequest;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.OrderSortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.model.IModel;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class JpaDataProvider<T extends Serializable> implements ISortableDataProvider<T, String>,
    IFilterStateLocator<Specification<T>> {

    @Getter
    private final OrderSortState sortState = new OrderSortState();

    private final JpaSpecificationExecutor<T> jpaSpecificationExecutor;
    private final IModel<Specification<T>> filterState;

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        Specification<T> spec = getFilterState();

        Sort sort = getSort();
        Pageable pageable = new OffsetPageRequest(first, count, sort);

        Page<T> page = jpaSpecificationExecutor.findAll(spec, pageable);
        List<T> content = page.getContent();
        return content.iterator();
    }

    public Sort getSort() {
        return getOrder()
            .map(Sort::by)
            .orElseGet(Sort::unsorted);
    }

    public Optional<Order> getOrder() {
        return sortState.getOrder();
    }

    public void setOrder(@Nullable Order order) {
        sortState.setOrder(order);
    }

    public void setOrder(@NonNull String property, @NonNull SortOrder sortOrder) {
        sortState.setPropertySortOrder(property, sortOrder);
    }

    @Override
    public long size() {
        Specification<T> spec = getFilterState();
        return jpaSpecificationExecutor.count(spec);
    }

    @Override
    public Specification<T> getFilterState() {
        return filterState.getObject();
    }

    @Override
    public void setFilterState(Specification<T> state) {
        filterState.setObject(state);
    }

    @Override
    public void detach() {
        filterState.detach();
    }
}
