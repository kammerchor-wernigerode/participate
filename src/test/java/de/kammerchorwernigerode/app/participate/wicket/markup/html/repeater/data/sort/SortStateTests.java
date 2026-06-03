package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;

class SortStateTests {

    private SortState sortState;

    @BeforeEach
    void setUp() {
        sortState = new SortState();
    }

    @Test
    void initialState_shouldBeUnsorted() {
        assertThat(sortState.getSort().isUnsorted()).isTrue();
    }

    @Test
    void initialState_shouldReturnNoneForAnyProperties() {
        assertThat(sortState.getPropertySortOrder(new String[]{"name"})).isEqualTo(SortOrder.NONE);
    }

    @Test
    void initialState_shouldHaveNoProperties() {
        assertThat(sortState.getProperties()).isEmpty();
    }

    @Test
    void setPropertySortOrder_ascending_shouldSortAscending() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);

        assertThat(sortState.getSort().getOrderFor("name"))
            .isNotNull()
            .extracting(Sort.Order::getDirection).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void setPropertySortOrder_descending_shouldSortDescending() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.DESCENDING);

        assertThat(sortState.getSort().getOrderFor("name"))
            .isNotNull()
            .extracting(Sort.Order::getDirection).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void setPropertySortOrder_multipleProperties_shouldSortByAllProperties() {
        sortState.setPropertySortOrder(new String[]{"lastName", "firstName"}, SortOrder.ASCENDING);

        Sort sort = sortState.getSort();
        assertThat(sort.getOrderFor("lastName")).isNotNull();
        assertThat(sort.getOrderFor("firstName")).isNotNull();
    }

    @Test
    void setPropertySortOrder_noneForCurrentProperties_shouldClearSort() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.NONE);

        assertThat(sortState.getSort().isUnsorted()).isTrue();
    }

    @Test
    void setPropertySortOrder_noneForDifferentProperties_shouldNotClearSort() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);
        sortState.setPropertySortOrder(new String[]{"other"}, SortOrder.NONE);

        assertThat(sortState.getSort().isSorted()).isTrue();
    }

    @Test
    void setPropertySortOrder_noneWithNoExistingSort_shouldDoNothing() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.NONE);

        assertThat(sortState.getSort().isUnsorted()).isTrue();
    }

    @Test
    void setPropertySortOrder_overwritingExistingSort_shouldReplaceSort() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);
        sortState.setPropertySortOrder(new String[]{"date"}, SortOrder.DESCENDING);

        assertThat(sortState.getProperties()).containsExactly("date");
    }

    @Test
    void getPropertySortOrder_forCurrentlyAscendingProperties_shouldReturnAscending() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);

        assertThat(sortState.getPropertySortOrder(new String[]{"name"})).isEqualTo(SortOrder.ASCENDING);
    }

    @Test
    void getPropertySortOrder_forCurrentlyDescendingProperties_shouldReturnDescending() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.DESCENDING);

        assertThat(sortState.getPropertySortOrder(new String[]{"name"})).isEqualTo(SortOrder.DESCENDING);
    }

    @Test
    void getPropertySortOrder_forDifferentProperties_shouldReturnNone() {
        sortState.setPropertySortOrder(new String[]{"name"}, SortOrder.ASCENDING);

        assertThat(sortState.getPropertySortOrder(new String[]{"other"})).isEqualTo(SortOrder.NONE);
    }

    @Test
    void getPropertySortOrder_whenUnsorted_shouldReturnNone() {
        assertThat(sortState.getPropertySortOrder(new String[]{"name"})).isEqualTo(SortOrder.NONE);
    }

    @Test
    void getPropertySortOrder_forPartialMatch_shouldReturnNone() {
        sortState.setPropertySortOrder(new String[]{"lastName", "firstName"}, SortOrder.ASCENDING);

        assertThat(sortState.getPropertySortOrder(new String[]{"lastName"})).isEqualTo(SortOrder.NONE);
    }

    @Test
    void getProperties_afterSorting_shouldReturnAllSortedProperties() {
        sortState.setPropertySortOrder(new String[]{"lastName", "firstName"}, SortOrder.ASCENDING);

        assertThat(sortState.getProperties()).containsExactly("lastName", "firstName");
    }

    @Test
    void getProperties_whenUnsorted_shouldReturnEmptyArray() {
        assertThat(sortState.getProperties()).isEmpty();
    }

    @Test
    void setPropertySortOrder_nullProperties_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sortState.setPropertySortOrder(null, SortOrder.ASCENDING));
    }

    @Test
    void setPropertySortOrder_nullSortOrder_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sortState.setPropertySortOrder(new String[]{"name"}, null));
    }

    @Test
    void getPropertySortOrder_nullProperties_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> sortState.getPropertySortOrder(null));
    }
}
