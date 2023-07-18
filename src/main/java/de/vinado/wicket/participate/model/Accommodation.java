package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import static de.vinado.wicket.participate.model.Accommodation.Status.OFFERING;
import static de.vinado.wicket.participate.model.Accommodation.Status.SEARCHING;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation implements Serializable {

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private Integer beds;

    public Accommodation(@NonNull Status status, @Nullable Integer beds) {
        assertNotNegative(beds, "Amount of beds must not be negative");
        this.status = status;
        this.beds = beds;
    }

    private static void assertNotNegative(Integer beds, String message) {
        if (null != beds && beds < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static Accommodation noNeed() {
        return new Accommodation(Status.NO_NEED, null);
    }

    @Transient
    public boolean isQuantifiable() {
        return status.quantifiable;
    }

    @Transient
    public boolean isSearching() {
        return SEARCHING.equals(status);
    }

    @Transient
    public boolean isOffering() {
        return OFFERING.equals(status);
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Status {

        SEARCHING(true),
        OFFERING(true),
        NO_NEED(false),
        ;

        @Getter
        private final boolean quantifiable;
    }
}
