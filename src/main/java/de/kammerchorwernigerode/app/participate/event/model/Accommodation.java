package de.kammerchorwernigerode.app.participate.event.model;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

public record Accommodation() {


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Status {

        SEARCHING,
        OFFERING,
        NO_NEED,
        ;
    }
}
