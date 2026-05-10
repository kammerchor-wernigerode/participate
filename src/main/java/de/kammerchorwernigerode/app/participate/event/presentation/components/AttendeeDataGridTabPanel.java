package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.components.AttendeeDataGrid.Event.ItemUpdated;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceSummaryEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsDataProvider;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsSpecification;
import de.kammerchorwernigerode.app.participate.wicket.behavior.UpdateOnEventBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.Serializable;

public class AttendeeDataGridTabPanel extends GenericPanel<AttendeeDataGridTabPanel.Data> {

    private static final String[] SORT_PROPERTIES = {
        "invitationStatusOrder",
        "voiceOrder",
        "fileName",
        "firstName",
        "lastName",
    };

    @SpringBean
    private AttendeeDetailsEntryRepository attendeeDetailsEntryRepository;

    public AttendeeDataGridTabPanel(String id, IModel<AttendeeDataGridTabPanel.Data> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<Data> model = getModel();
        IModel<AttendeeDetailsSpecification> filterState = model.map(Data::getSpecification);
        AttendeeDetailsDataProvider dataProvider =
            new AttendeeDetailsDataProvider(attendeeDetailsEntryRepository, filterState);
        dataProvider.setSort(SORT_PROPERTIES, SortOrder.ASCENDING);

        AttendeeDataGrid dataGrid = new AttendeeDataGrid("dataGrid", dataProvider);
        add(dataGrid);

        IModel<AttendanceSummaryEntry> attendanceSummaryModel = model.flatMap(Data::getAttendanceSummaryModel);
        EventSummaryPanel eventSummary = new EventSummaryPanel("eventSummary", attendanceSummaryModel);
        eventSummary.add(new UpdateOnEventBehavior<>(ItemUpdated.class));
        add(eventSummary);
    }

    @lombok.Data
    public static class Data implements Serializable {

        private AttendeeDetailsSpecification specification;
        private IModel<AttendanceSummaryEntry> attendanceSummaryModel;
    }
}
