package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.data.domain.OffsetPageRequest;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

import java.io.Serializable;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class SortableSpringDataProvider<T extends Serializable, S extends Serializable>
    extends SpringDataProvider<T> implements ISortableDataProvider<T, S> {

    @Getter
    private final SpringSortState<S> sortState;

    @Override
    public Streamable<T> streamable(long offset, long pageSize) {
        Sort sort = getSort();
        Pageable pageable = new OffsetPageRequest(offset, pageSize, sort);
        return streamable(pageable);
    }

    public abstract Streamable<T> streamable(Pageable pageable);

    public Sort getSort() {
        return sortState.getSort();
    }

    public void setPropertySortOrder(@NonNull S property, @NonNull SortOrder sortOrder) {
        sortState.setPropertySortOrder(property, sortOrder);
    }
}
