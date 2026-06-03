package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;

class OrderStateTests {

    private OrderState orderState;

    @BeforeEach
    void setUp() {
        orderState = new OrderState();
    }

    @Test
    void initialState_shouldHaveNoOrder() {
        assertThat(orderState.getOrder()).isEmpty();
    }

    @Test
    void initialState_shouldReturnUnsortedSort() {
        assertThat(orderState.getSort().isUnsorted()).isTrue();
    }

    @Test
    void initialState_shouldReturnNoneForAnyProperty() {
        assertThat(orderState.getPropertySortOrder("name")).isEqualTo(SortOrder.NONE);
    }

    @Test
    void setPropertySortOrder_ascending_shouldSetAscendingOrder() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);

        assertThat(orderState.getOrder())
            .hasValueSatisfying(order -> {
                assertThat(order.getProperty()).isEqualTo("name");
                assertThat(order.getDirection()).isEqualTo(Sort.Direction.ASC);
            });
    }

    @Test
    void setPropertySortOrder_descending_shouldSetDescendingOrder() {
        orderState.setPropertySortOrder("name", SortOrder.DESCENDING);

        assertThat(orderState.getOrder())
            .hasValueSatisfying(order -> {
                assertThat(order.getProperty()).isEqualTo("name");
                assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
            });
    }

    @Test
    void setPropertySortOrder_changingProperty_shouldOverwritePreviousOrder() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);
        orderState.setPropertySortOrder("date", SortOrder.DESCENDING);

        assertThat(orderState.getOrder())
            .hasValueSatisfying(order -> assertThat(order.getProperty()).isEqualTo("date"));
    }

    @Test
    void setPropertySortOrder_noneForCurrentProperty_shouldClearOrder() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);
        orderState.setPropertySortOrder("name", SortOrder.NONE);

        assertThat(orderState.getOrder()).isEmpty();
    }

    @Test
    void setPropertySortOrder_noneForDifferentProperty_shouldNotClearOrder() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);
        orderState.setPropertySortOrder("other", SortOrder.NONE);

        assertThat(orderState.getOrder()).isPresent();
    }

    @Test
    void setPropertySortOrder_noneWithNoExistingOrder_shouldDoNothing() {
        orderState.setPropertySortOrder("name", SortOrder.NONE);

        assertThat(orderState.getOrder()).isEmpty();
    }

    @Test
    void getPropertySortOrder_forCurrentlyAscendingProperty_shouldReturnAscending() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);

        assertThat(orderState.getPropertySortOrder("name")).isEqualTo(SortOrder.ASCENDING);
    }

    @Test
    void getPropertySortOrder_forCurrentlyDescendingProperty_shouldReturnDescending() {
        orderState.setPropertySortOrder("name", SortOrder.DESCENDING);

        assertThat(orderState.getPropertySortOrder("name")).isEqualTo(SortOrder.DESCENDING);
    }

    @Test
    void getPropertySortOrder_forDifferentProperty_shouldReturnNone() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);

        assertThat(orderState.getPropertySortOrder("other")).isEqualTo(SortOrder.NONE);
    }

    @Test
    void getPropertySortOrder_withNoOrderSet_shouldReturnNone() {
        assertThat(orderState.getPropertySortOrder("name")).isEqualTo(SortOrder.NONE);
    }

    @Test
    void getSort_withOrder_shouldReturnSortForThatProperty() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);

        Sort sort = orderState.getSort();

        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.getOrderFor("name"))
            .isNotNull()
            .extracting(Sort.Order::getDirection).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void getSort_withoutOrder_shouldReturnUnsorted() {
        assertThat(orderState.getSort().isUnsorted()).isTrue();
    }

    @Test
    void getOrder_afterSettingOrder_shouldReturnPresentOptional() {
        orderState.setPropertySortOrder("name", SortOrder.ASCENDING);

        assertThat(orderState.getOrder()).isPresent();
    }

    @Test
    void setPropertySortOrder_nullProperty_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderState.setPropertySortOrder(null, SortOrder.ASCENDING));
    }

    @Test
    void setPropertySortOrder_nullSortOrder_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderState.setPropertySortOrder("name", null));
    }

    @Test
    void getPropertySortOrder_nullProperty_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> orderState.getPropertySortOrder(null));
    }
}
