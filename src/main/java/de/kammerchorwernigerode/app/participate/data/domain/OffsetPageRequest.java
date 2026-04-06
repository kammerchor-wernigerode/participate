package de.kammerchorwernigerode.app.participate.data.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class OffsetPageRequest implements Pageable {

    private final long first;
    private final long count;
    private final Sort sort;

    public OffsetPageRequest(long first, long count, Sort sort) {
        Assert.isTrue(first >= 0, "First must not be negative");
        Assert.isTrue(count > 0, "Count must be positive");

        this.first = first;
        this.count = count;
        this.sort = null == sort ? Sort.unsorted() : sort;
    }

    @Override
    public int getPageNumber() {
        return Math.toIntExact(first / count);
    }

    @Override
    public int getPageSize() {
        return Math.toIntExact(count);
    }

    @Override
    public long getOffset() {
        return first;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetPageRequest(first + count, count, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
            ? new OffsetPageRequest(first - count, count, sort)
            : first();
    }

    @Override
    public Pageable first() {
        return new OffsetPageRequest(0, count, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        Assert.isTrue(pageNumber >= 0, "Page number must not be negative");

        return new OffsetPageRequest((long) pageNumber * count, count, sort);
    }

    @Override
    public boolean hasPrevious() {
        return first >= count;
    }

}
