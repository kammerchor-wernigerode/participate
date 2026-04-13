package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class SortState implements ISortState<String[]>, IClusterable {

    @NonNull
    private Sort sort = Sort.unsorted();

    @Override
    public void setPropertySortOrder(@NonNull String[] properties, @NonNull SortOrder sortOrder) {
        if (SortOrder.NONE == sortOrder) {
            String[] sortProperties = getProperties();
            if (sort.isSorted() && Arrays.compare(properties, sortProperties) == 0) {
                sort = Sort.unsorted();
            }
        } else {
            Direction direction = translate(sortOrder);
            sort = Sort.by(direction, properties);
        }
    }

    @Override
    public SortOrder getPropertySortOrder(@NonNull String[] properties) {
        String[] sortProperties = getProperties();
        if (sort.isUnsorted() || Arrays.compare(sortProperties, properties) != 0) {
            return SortOrder.NONE;
        }

        return Arrays.stream(properties)
            .map(sort::getOrderFor)
            .filter(Objects::nonNull)
            .map(Order::getDirection)
            .map(SortState::translate)
            .findAny()
            .orElse(SortOrder.NONE);
    }

    public String[] getProperties() {
        return sort.stream()
            .map(Order::getProperty)
            .toArray(String[]::new);
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
