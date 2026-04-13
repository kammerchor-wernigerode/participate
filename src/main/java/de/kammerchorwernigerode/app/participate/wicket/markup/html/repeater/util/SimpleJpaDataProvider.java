package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.OrderSortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;
import java.util.Optional;

import lombok.Getter;
import lombok.NonNull;

public abstract class SimpleJpaDataProvider<T extends Serializable> extends JpaDataProvider<T, String> {

    @Getter
    private final OrderSortState sortState = new OrderSortState();

    public SimpleJpaDataProvider(JpaSpecificationExecutor<T> jpaSpecificationExecutor,
                                 IModel<Specification<T>> filterState) {
        super(jpaSpecificationExecutor, filterState);
    }

    @Override
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
}
