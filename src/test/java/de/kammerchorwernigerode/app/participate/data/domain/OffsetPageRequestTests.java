package de.kammerchorwernigerode.app.participate.data.domain;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;

class OffsetPageRequestTests {

    @Test
    void constructor_withValidArguments_shouldStoreOffsetAndPageSize() {
        OffsetPageRequest request = new OffsetPageRequest(10L, 5, Sort.unsorted());

        assertThat(request.getOffset()).isEqualTo(10L);
        assertThat(request.getPageSize()).isEqualTo(5);
    }

    @Test
    void constructor_withLongPageSize_shouldConvertToInt() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 20L, Sort.unsorted());

        assertThat(request.getPageSize()).isEqualTo(20);
    }

    @Test
    void constructor_withNullSort_shouldDefaultToUnsorted() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, null);

        assertThat(request.getSort().isUnsorted()).isTrue();
    }

    @Test
    void constructor_withSort_shouldStoreSort() {
        Sort sort = Sort.by("name");
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, sort);

        assertThat(request.getSort()).isEqualTo(sort);
    }

    @Test
    void constructor_withNegativeOffset_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new OffsetPageRequest(-1L, 10, null))
            .withMessageContaining("Offset must not be negative");
    }

    @Test
    void constructor_withZeroPageSize_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new OffsetPageRequest(0L, 0, null))
            .withMessageContaining("Page size must not be less than one");
    }

    @Test
    void constructor_withNegativePageSize_shouldThrowIllegalArgumentException() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new OffsetPageRequest(0L, -1, null))
            .withMessageContaining("Page size must not be less than one");
    }

    @Test
    void constructor_withZeroOffset_shouldBeValid() {
        assertThatNoException()
            .isThrownBy(() -> new OffsetPageRequest(0L, 1, null));
    }

    @Test
    void getPageNumber_shouldReturnOffsetDividedByPageSize() {
        OffsetPageRequest request = new OffsetPageRequest(20L, 5, null);

        assertThat(request.getPageNumber()).isEqualTo(4);
    }

    @Test
    void getPageNumber_whenOffsetIsZero_shouldReturnZero() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, null);

        assertThat(request.getPageNumber()).isEqualTo(0);
    }

    @Test
    void getPageNumber_whenOffsetIsLessThanPageSize_shouldReturnZero() {
        OffsetPageRequest request = new OffsetPageRequest(3L, 10, null);

        assertThat(request.getPageNumber()).isEqualTo(0);
    }

    @Test
    void next_shouldIncrementOffsetByPageSize() {
        OffsetPageRequest request = new OffsetPageRequest(10L, 5, null);

        Pageable next = request.next();

        assertThat(next.getOffset()).isEqualTo(15L);
        assertThat(next.getPageSize()).isEqualTo(5);
    }

    @Test
    void next_shouldPreserveSort() {
        Sort sort = Sort.by("name");
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, sort);

        assertThat(request.next().getSort()).isEqualTo(sort);
    }

    @Test
    void hasPrevious_whenOffsetEqualsPageSize_shouldReturnTrue() {
        OffsetPageRequest request = new OffsetPageRequest(10L, 10, null);

        assertThat(request.hasPrevious()).isTrue();
    }

    @Test
    void hasPrevious_whenOffsetGreaterThanPageSize_shouldReturnTrue() {
        OffsetPageRequest request = new OffsetPageRequest(20L, 10, null);

        assertThat(request.hasPrevious()).isTrue();
    }

    @Test
    void hasPrevious_whenOffsetLessThanPageSize_shouldReturnFalse() {
        OffsetPageRequest request = new OffsetPageRequest(5L, 10, null);

        assertThat(request.hasPrevious()).isFalse();
    }

    @Test
    void hasPrevious_whenOffsetIsZero_shouldReturnFalse() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, null);

        assertThat(request.hasPrevious()).isFalse();
    }

    @Test
    void previousOrFirst_whenHasPrevious_shouldDecrementOffsetByPageSize() {
        OffsetPageRequest request = new OffsetPageRequest(20L, 10, null);

        Pageable previous = request.previousOrFirst();

        assertThat(previous.getOffset()).isEqualTo(10L);
    }

    @Test
    void previousOrFirst_whenNoPrevious_shouldReturnFirst() {
        OffsetPageRequest request = new OffsetPageRequest(5L, 10, null);

        Pageable first = request.previousOrFirst();

        assertThat(first.getOffset()).isEqualTo(0L);
    }

    @Test
    void previousOrFirst_shouldPreservePageSizeAndSort() {
        Sort sort = Sort.by("name");
        OffsetPageRequest request = new OffsetPageRequest(10L, 5, sort);

        Pageable previous = request.previousOrFirst();

        assertThat(previous.getPageSize()).isEqualTo(5);
        assertThat(previous.getSort()).isEqualTo(sort);
    }

    @Test
    void first_shouldReturnPageableWithZeroOffset() {
        OffsetPageRequest request = new OffsetPageRequest(30L, 10, null);

        Pageable first = request.first();

        assertThat(first.getOffset()).isEqualTo(0L);
    }

    @Test
    void first_shouldPreservePageSizeAndSort() {
        Sort sort = Sort.by("name");
        OffsetPageRequest request = new OffsetPageRequest(30L, 10, sort);

        Pageable first = request.first();

        assertThat(first.getPageSize()).isEqualTo(10);
        assertThat(first.getSort()).isEqualTo(sort);
    }

    @Test
    void withPage_shouldComputeOffsetAsPageNumberTimesPageSize() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, null);

        Pageable page3 = request.withPage(3);

        assertThat(page3.getOffset()).isEqualTo(30L);
        assertThat(page3.getPageSize()).isEqualTo(10);
    }

    @Test
    void withPage_withPageNumberZero_shouldReturnZeroOffset() {
        OffsetPageRequest request = new OffsetPageRequest(20L, 10, null);

        Pageable page0 = request.withPage(0);

        assertThat(page0.getOffset()).isEqualTo(0L);
    }

    @Test
    void withPage_withNegativePageNumber_shouldThrowIllegalArgumentException() {
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, null);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> request.withPage(-1))
            .withMessageContaining("Page number must not be negative");
    }

    @Test
    void withPage_shouldPreserveSort() {
        Sort sort = Sort.by("name");
        OffsetPageRequest request = new OffsetPageRequest(0L, 10, sort);

        assertThat(request.withPage(1).getSort()).isEqualTo(sort);
    }
}
