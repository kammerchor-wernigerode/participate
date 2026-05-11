package de.kammerchorwernigerode.app.participate.event.presentation.components.details.overview;

import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EventDetailsPanel extends GenericPanel<EventProjection> {

    public EventDetailsPanel(String id, IModel<EventProjection> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventProjection> model = getModel();
        IModel<Long> eventId = model.map(EventProjection::getId);

        AttendeeEntrySpecification attendeeSpecification = new AttendeeEntrySpecification(eventId);
        IModel<AttendeeEntrySpecification> specModel = new CompoundPropertyModel<>(attendeeSpecification);
        DetailedAttendeeTablePanel attendeeTablePanel = new DetailedAttendeeTablePanel("tablePanel", specModel, model);
        add(attendeeTablePanel);
    }
}
