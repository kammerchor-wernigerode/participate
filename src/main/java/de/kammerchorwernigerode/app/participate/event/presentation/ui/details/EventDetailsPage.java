package de.kammerchorwernigerode.app.participate.event.presentation.ui.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.components.AttendeeDataGridTabPanel;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsSpecification;
import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.tabs.Tabs;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

public class EventDetailsPage extends ParticipatePage implements IGenericComponent<Long, EventDetailsPage> {

    @SpringBean
    private EventRecordRepository eventRecordRepository;

    public EventDetailsPage(PageParameters parameters) {
        super(parameters);

        setModel(new EventIdEntryModel(parameters, eventRecordRepository));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setLayout(Layout.FLUID);

        IModel<Long> model = getModel();

        List<ITab> tabs = createTabs(model);
        Tabs<ITab> tabbedPanel = new Tabs<>("tabs", tabs);
        add(tabbedPanel);
    }

    private List<ITab> createTabs(IModel<Long> model) {
        List<ITab> tabs = new ArrayList<>();

        AttendeeDataGridTabPanel.Data attendeesTabPanelData = new AttendeeDataGridTabPanel.Data();
        attendeesTabPanelData.setSpecification(new AttendeeDetailsSpecification(model));
        IModel<AttendeeDataGridTabPanel.Data> attendeesTabPanelModel =
            new CompoundPropertyModel<>(attendeesTabPanelData);
        tabs.add(new AttendeesTab(new ResourceModel("attendees"), attendeesTabPanelModel));

        return tabs;
    }


    private static class AttendeesTab extends AbstractTab {

        private final IModel<AttendeeDataGridTabPanel.Data> model;

        public AttendeesTab(IModel<String> title, IModel<AttendeeDataGridTabPanel.Data> model) {
            super(title);
            this.model = model;
        }

        @Override
        public AttendeeDataGridTabPanel getPanel(String panelId) {
            return new AttendeeDataGridTabPanel(panelId, model);
        }
    }

    @RequiredArgsConstructor
    private static class EventIdEntryModel extends LoadableDetachableModel<Long> {

        private final PageParameters parameters;
        private final EventRecordRepository eventRecordRepository;

        @Override
        protected Long load() {
            StringValue idParam = parameters.get("id");
            return findEventEntry(idParam);
        }

        private Long findEventEntry(StringValue idParam) {
            try {
                Long eventId = idParam.toOptionalLong();
                return Optional.ofNullable(eventId)
                    .filter(eventRecordRepository::existsById)
                    .orElseThrow();
            } catch (Exception e) {
                throw new ModelNotFoundException("Event w/ id=" + idParam + " could not be found", e);
            }
        }
    }
}
