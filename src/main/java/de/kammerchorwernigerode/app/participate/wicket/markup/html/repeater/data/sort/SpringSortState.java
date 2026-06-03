package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.io.IClusterable;
import org.springframework.data.domain.Sort;

public interface SpringSortState<T> extends ISortState<T>, IClusterable {

    Sort getSort();

    default Sort.Direction translate(SortOrder sortOrder) {
        return sortOrder == SortOrder.ASCENDING
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
    }

    default SortOrder translate(Sort.Direction direction) {
        return direction.isAscending()
            ? SortOrder.ASCENDING
            : SortOrder.DESCENDING;
    }
}
