package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Accommodation.Status;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.ui.event.AccommodationBadge;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class BnBIconPanel extends Panel {

    public BnBIconPanel(String id, IModel<Participant> model) {
        super(id, model);

        add(new AccommodationBadge("accommodation", accommodationModel(model)));

        IModel<ParticipantDTO> dtoModel = model.map(ParticipantDTO::new);
        WebMarkupContainer car = new WebMarkupContainer("car");
        car.add(new CssClassNameAppender(dtoModel.getObject().isCar() ? "bg-success" : "text-muted"));
        add(car);

        car.add(new Label("seats", dtoModel.map(ParticipantDTO::getCarSeatCount)));
    }

    private IModel<Accommodation> accommodationModel(IModel<Participant> model) {
        return model
            .map(Participant::getAccommodation)
            .orElseGet(this::defaultAccommodation);
    }

    private Accommodation defaultAccommodation() {
        return new Accommodation(Status.NO_NEED, null);
    }
}
