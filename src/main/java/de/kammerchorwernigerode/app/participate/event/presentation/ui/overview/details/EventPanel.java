package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.util.function.Predicate.not;

public class EventPanel extends GenericPanel<EventEntry> {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    public EventPanel(String id, IModel<EventEntry> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventEntry> model = getModel();

        Label title = new Label("title", model.map(this::printTitle));
        title.setRenderBodyOnly(true);
        add(title);

        IModel<AttendeeEntrySpecification> specModel = model.map(this::createAttendeeEntrySpecification)
            .flatMap(CompoundPropertyModel::new);
        AttendeeTablePanel attendeeTablePanel = new AttendeeTablePanel("attendeeTablePanel", specModel);
        add(attendeeTablePanel);
    }

    private String printTitle(EventEntry entry) {
        DateTimeFormatter formatter = MONTH_FORMATTER.localizedBy(getLocale());
        String monthYear = formatter.format(entry.getStart());
        String name = Optional.ofNullable(entry.getSummary())
            .filter(not(Strings::isEmpty))
            .orElseGet(() -> getString("event"));
        return name + " " + monthYear;
    }

    private AttendeeEntrySpecification createAttendeeEntrySpecification(EventEntry event) {
        Long eventId = event.getId();
        return new AttendeeEntrySpecification(eventId);
    }
}
