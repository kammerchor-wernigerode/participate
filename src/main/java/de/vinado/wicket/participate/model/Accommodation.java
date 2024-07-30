package de.vinado.wicket.participate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.util.lang.Objects;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Optional;

import static de.vinado.wicket.participate.model.Accommodation.Status.OFFERING;
import static de.vinado.wicket.participate.model.Accommodation.Status.SEARCHING;

@Data
@Embeddable
@NoArgsConstructor
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

    @Transient
    public boolean isQuantifiable() {
        return Optional.ofNullable(status)
            .map(Status::isQuantifiable)
            .orElse(false);
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

        private final boolean quantifiable;

        public boolean isQuantifiable() {
            return Objects.defaultIfNull(quantifiable, false);
        }
    }
}
