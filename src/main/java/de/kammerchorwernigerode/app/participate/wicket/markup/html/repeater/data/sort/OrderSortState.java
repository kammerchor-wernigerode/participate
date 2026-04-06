package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.io.IClusterable;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.Optional;

import lombok.NonNull;
import lombok.Setter;

public class OrderSortState implements ISortState<String>, IClusterable {

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

    public Optional<Order> getOrder() {
        return Optional.ofNullable(order);
    }

    private static Direction translate(SortOrder sortOrder) {
        return sortOrder == SortOrder.ASCENDING
            ? Direction.ASC
            : Direction.DESC;
    }

    private static SortOrder translate(Direction direction) {
        return direction.isAscending()
            ? SortOrder.ASCENDING
            : SortOrder.DESCENDING;
    }
}
