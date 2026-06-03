package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;

class SpringSortStateTests {

    private SpringSortState<String> sortState;

    @BeforeEach
    void setUp() {
        sortState = new SpringSortState<>() {

            @Override
            public Sort getSort() {
                return Sort.unsorted();
            }

            @Override
            public void setPropertySortOrder(String property, SortOrder order) {
            }

            @Override
            public SortOrder getPropertySortOrder(String property) {
                return SortOrder.NONE;
            }
        };
    }

    @Test
    void translate_ascending_shouldReturnAsc() {
        assertThat(sortState.translate(SortOrder.ASCENDING)).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void translate_descending_shouldReturnDesc() {
        assertThat(sortState.translate(SortOrder.DESCENDING)).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void translate_none_shouldReturnDesc() {
        assertThat(sortState.translate(SortOrder.NONE)).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void translate_directionAsc_shouldReturnAscending() {
        assertThat(sortState.translate(Sort.Direction.ASC)).isEqualTo(SortOrder.ASCENDING);
    }

    @Test
    void translate_directionDesc_shouldReturnDescending() {
        assertThat(sortState.translate(Sort.Direction.DESC)).isEqualTo(SortOrder.DESCENDING);
    }
}
