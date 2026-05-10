package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceSummaryEntry;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class EventSummaryPanel extends GenericPanel<AttendanceSummaryEntry> {

    public EventSummaryPanel(String id, IModel<AttendanceSummaryEntry> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<AttendanceSummaryEntry> model = getModel();

        Label attendeeCount = new Label("attendeeCount", model.map(AttendanceSummaryEntry::attendeeCount));
        add(attendeeCount);

        Label requiredBedCount = new Label("requiredBedCount", model.map(AttendanceSummaryEntry::requiredBedCount));
        add(requiredBedCount);

        Label permanentCount = new Label("permanentCount", model.map(AttendanceSummaryEntry::permanentCount));
        add(permanentCount);

        Label maxCount = new Label("maxCount", model.map(AttendanceSummaryEntry::maxCount));
        add(maxCount);
    }
}
