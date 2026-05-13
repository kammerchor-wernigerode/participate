package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;

public record Car(
    boolean available,
    short seats) implements Serializable {

    public Car(Short seats) {
        this(seats >= 0, seats < 0 ? 0 : seats);
    }
}
