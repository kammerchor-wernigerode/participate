package de.kammerchorwernigerode.app.participate.data.domain;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import lombok.Getter;

@Getter
public class OffsetPageRequest implements Pageable {

    private final long offset;
    private final int pageSize;

    @NonNull
    private final Sort sort;

    public OffsetPageRequest(long offset, long pageSize, @Nullable Sort sort) {
        this(offset, Math.toIntExact(pageSize), sort);
    }

    public OffsetPageRequest(long offset, int pageSize, @Nullable Sort sort) {
        Assert.isTrue(offset >= 0, "Offset must not be negative");
        Assert.isTrue(pageSize > 0, "Page size must not be less than one");

        this.offset = offset;
        this.pageSize = pageSize;
        this.sort = null == sort ? Sort.unsorted() : sort;
    }

    @Override
    public int getPageNumber() {
        return Math.toIntExact(offset / pageSize);
    }

    @Override
    @NonNull
    public Pageable next() {
        return new OffsetPageRequest(offset + pageSize, pageSize, sort);
    }

    @Override
    @NonNull
    public Pageable previousOrFirst() {
        return hasPrevious()
            ? new OffsetPageRequest(offset - pageSize, pageSize, sort)
            : first();
    }

    @Override
    @NonNull
    public Pageable first() {
        return new OffsetPageRequest(0, pageSize, sort);
    }

    @Override
    @NonNull
    public Pageable withPage(int pageNumber) {
        Assert.isTrue(pageNumber >= 0, "Page number must not be negative");

        return new OffsetPageRequest((long) pageNumber * pageSize, pageSize, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset >= pageSize;
    }
}
