package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SortableSpringDataProviderTests {

    @Mock
    private SpringSortState<String> sortState;

    @Mock
    private Streamable<String> streamable;

    private SortableSpringDataProvider<String, String> provider;
    private Pageable capturedPageable;

    @BeforeEach
    void setUp() {
        provider = new SortableSpringDataProvider<>(sortState) {

            @Override
            public Streamable<String> streamable(Pageable pageable) {
                capturedPageable = pageable;
                return streamable;
            }

            @Override
            public long size() {
                return 0;
            }
        };
    }

    @Test
    void getSortState_shouldReturnProvidedSortState() {
        assertThat(provider.getSortState()).isSameAs(sortState);
    }

    @Test
    void getSort_shouldDelegateToSortState() {
        Sort sort = Sort.by("name");
        when(sortState.getSort()).thenReturn(sort);

        assertThat(provider.getSort()).isSameAs(sort);
    }

    @Test
    void streamable_shouldPassCorrectOffsetToPageable() {
        when(sortState.getSort()).thenReturn(Sort.unsorted());

        provider.streamable(5L, 10L);

        assertThat(capturedPageable.getOffset()).isEqualTo(5L);
    }

    @Test
    void streamable_shouldPassCorrectPageSizeToPageable() {
        when(sortState.getSort()).thenReturn(Sort.unsorted());

        provider.streamable(0L, 10L);

        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
    }

    @Test
    void streamable_shouldIncludeSortFromSortStateInPageable() {
        Sort sort = Sort.by("name");
        when(sortState.getSort()).thenReturn(sort);

        provider.streamable(0L, 10L);

        assertThat(capturedPageable.getSort()).isEqualTo(sort);
    }

    @Test
    void streamable_shouldReturnResultFromDelegateMethod() {
        when(sortState.getSort()).thenReturn(Sort.unsorted());

        assertThat(provider.streamable(0L, 10L)).isSameAs(streamable);
    }

    @Test
    void setPropertySortOrder_shouldDelegateToSortState() {
        provider.setPropertySortOrder("name", SortOrder.ASCENDING);

        verify(sortState).setPropertySortOrder("name", SortOrder.ASCENDING);
    }

    @Test
    void setPropertySortOrder_nullProperty_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> provider.setPropertySortOrder(null, SortOrder.ASCENDING));
    }

    @Test
    void setPropertySortOrder_nullSortOrder_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> provider.setPropertySortOrder("name", null));
    }
}
