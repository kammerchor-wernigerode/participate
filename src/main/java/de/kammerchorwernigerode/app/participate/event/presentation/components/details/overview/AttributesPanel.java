package de.kammerchorwernigerode.app.participate.event.presentation.components.details.overview;

import de.kammerchorwernigerode.app.participate.event.model.Accommodation;
import de.kammerchorwernigerode.app.participate.event.model.Accommodation.Status;
import de.kammerchorwernigerode.app.participate.event.presentation.components.attendee.AccommodationBadge;
import de.kammerchorwernigerode.app.participate.event.presentation.components.attendee.CarBadge;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeProjection.Attributes;
import de.kammerchorwernigerode.app.participate.event.presentation.model.Car;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Objects;

public class AttributesPanel extends GenericPanel<Attributes> {

    public AttributesPanel(String id, IModel<? extends Attributes> model) {
        super(id, model.map(SerializableFunction.identity()));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<Attributes> model = getModel();

        IModel<Accommodation> accommodationModel = model.map(this::accommodation);
        AccommodationBadge accommodationBadge = new AccommodationBadge("accommodation", accommodationModel);
        add(accommodationBadge);

        IModel<Car> carModel = model.map(this::car);
        CarBadge carBadge = new CarBadge("car", carModel);
        add(carBadge);
    }

    private Accommodation accommodation(Attributes attributes) {
        Status status = Objects.requireNonNullElse(attributes.getAccommodationStatus(), Status.NO_NEED);
        Integer bedCount = attributes.getAccommodationBedCount();
        return new Accommodation(status, bedCount);
    }

    private Car car(Attributes attributes) {
        Short carSeatCount = attributes.getCarSeatCount();
        return new Car(carSeatCount);
    }
}
