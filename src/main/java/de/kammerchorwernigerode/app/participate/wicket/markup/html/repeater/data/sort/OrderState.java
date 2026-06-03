package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.Optional;

import lombok.NonNull;
import lombok.Setter;

public class OrderState implements SpringSortState<String> {

    @Nullable
    @Setter
    private Order order;

    @Override
    public void setPropertySortOrder(@NonNull String property, @NonNull SortOrder sortOrder) {
        if (sortOrder == SortOrder.NONE) {
            if (null != order && property.equals(order.getProperty())) {
                order = null;
            }
        } else {
            Direction direction = translate(sortOrder);
            order = new Order(direction, property);
        }
    }

    @Override
    public SortOrder getPropertySortOrder(@NonNull String property) {
        if (null == order || !order.getProperty().equals(property)) {
            return SortOrder.NONE;
        }
        return translate(order.getDirection());
    }

    @Override
    public Sort getSort() {
        return getOrder()
            .map(Sort::by)
            .orElseGet(Sort::unsorted);
    }

    public Optional<Order> getOrder() {
        return Optional.ofNullable(order);
    }
}
