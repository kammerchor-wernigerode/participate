package de.kammerchorwernigerode.app.participate.event.model;

import org.springframework.util.Assert;

import java.io.Serializable;

import lombok.NonNull;

public record Accommodation(@NonNull Status status, Integer beds) implements Serializable {

    public Accommodation {
        Assert.isTrue(beds == null || beds >= 0, "Amount of beds must not be negative");
    }


    public enum Status {

        SEARCHING,
        OFFERING,
        NO_NEED,
        ;
    }
}
