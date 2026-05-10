package de.kammerchorwernigerode.app.participate.event.presentation.ui.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.AccommodationStatus;
import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.event.infrastructure.EventReference;
import de.kammerchorwernigerode.app.participate.event.presentation.EventPeriodDatePrinter;
import de.kammerchorwernigerode.app.participate.event.presentation.EventTitlePrinter;
import de.kammerchorwernigerode.app.participate.event.presentation.components.AttendeeDataGridTabPanel;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceSummaryEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsSpecification;
import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.ParticipateCssResourceReference;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.components.TooltipBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.tabs.Tabs;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.CssContentHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;

public class EventDetailsPage extends ParticipatePage implements IGenericComponent<EventProjection, EventDetailsPage> {

    @SpringBean
    private EventRecordRepository eventRecordRepository;

    @SpringBean
    private EventTitlePrinter eventTitlePrinter;

    @SpringBean
    private EventPeriodDatePrinter eventPeriodDatePrinter;

    public EventDetailsPage(PageParameters parameters) {
        super(parameters);

        setModel(new EventModel(parameters, eventRecordRepository));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setLayout(Layout.FLUID);

        PageParameters parameters = getPageParameters();
        IModel<EventProjection> model = getModel();

        List<ITab> tabs = createTabs(model);
        Tabs<ITab> tabbedPanel = new Tabs<>("tabs", tabs);
        tabbedPanel.setSelectedTab(parameters.get("tab").toInt(0));
        add(tabbedPanel);


        IModel<EventProjection> previousEvent = model.map(this::previousEvent);
        Boolean hasPrevious = previousEvent.isPresent().getObject();
        PageParameters previousParameters = new PageParameters(parameters)
            .set("id", previousEvent.map(EventReference::getId).getObject());
        BookmarkablePageLink<Void> previousLink = new BookmarkablePageLink<>("previousLink", EventDetailsPage.class,
            previousParameters);
        previousLink.setEnabled(previousEvent.isPresent().getObject());
        if (hasPrevious) {
            previousLink.add(new TooltipBehavior(previousEvent.map(event -> eventTitlePrinter
                .print(event, getLocale()))));
        } else {
            previousLink.add(ClassAttributeModifier.append("class", "disabled"));
            previousLink.add(AttributeModifier.replace("aria-disabled", true));
        }
        add(previousLink);

        Label titleLabel = new Label("title", model.map(event -> eventTitlePrinter.print(event, getLocale())));
        titleLabel.add(new TooltipBehavior(model.map(event -> eventPeriodDatePrinter.print(event, getLocale()))));
        add(titleLabel);

        IModel<EventProjection> nextEvent = model.map(this::nextEvent);
        Boolean hasNext = nextEvent.isPresent().getObject();
        PageParameters nextParameters = new PageParameters(parameters)
            .set("id", nextEvent.map(EventReference::getId).getObject());
        BookmarkablePageLink<Void> nextLink = new BookmarkablePageLink<>("nextLink", EventDetailsPage.class,
            nextParameters);
        nextLink.setEnabled(hasNext);
        if (hasNext) {
            nextLink.add(new TooltipBehavior(nextEvent.map(event -> eventTitlePrinter
                .print(event, getLocale()))));
        } else {
            nextLink.add(ClassAttributeModifier.append("class", "disabled"));
            nextLink.add(AttributeModifier.replace("aria-disabled", true));
        }
        add(nextLink);
    }

    private EventProjection previousEvent(EventProjection event) {
        Instant startInstant = event.getStartInstant();
        return eventRecordRepository.findFirstByStartInstantLessThanOrderByStartInstantDesc(startInstant)
            .orElse(null);
    }

    private EventProjection nextEvent(EventProjection event) {
        Instant startInstant = event.getStartInstant();
        return eventRecordRepository.findFirstByStartInstantGreaterThanOrderByStartInstantAsc(startInstant)
            .orElse(null);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssContentHeaderItem.forCSS("""
            body {
                padding-bottom: 4.5rem;
            }\
            """, "event-details-page"));
        response.render(ParticipateCssResourceReference.asHeaderItem());
    }

    private List<ITab> createTabs(IModel<EventProjection> model) {
        List<ITab> tabs = new ArrayList<>();

        IModel<Long> eventIdModel = model.map(EventProjection::getId);

        AttendeeDataGridTabPanel.Data attendeesTabPanelData = new AttendeeDataGridTabPanel.Data();
        attendeesTabPanelData.setSpecification(new AttendeeDetailsSpecification(eventIdModel));
        AttendanceSummaryModel attendanceSummaryModel = new AttendanceSummaryModel(eventIdModel, eventRecordRepository);
        attendeesTabPanelData.setAttendanceSummaryModel(attendanceSummaryModel);
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
    private static class EventModel extends LoadableDetachableModel<EventProjection> {

        private final PageParameters parameters;
        private final EventRecordRepository eventRecordRepository;

        @Override
        protected EventProjection load() {
            StringValue idParam = parameters.get("id");
            return findEventProjection(idParam);
        }

        private EventProjection findEventProjection(StringValue idParam) {
            try {
                Long eventId = idParam.toOptionalLong();
                return eventRecordRepository.findProjectionById(eventId)
                    .orElseThrow();
            } catch (Exception e) {
                throw new ModelNotFoundException("Event w/ id=" + idParam + " could not be found", e);
            }
        }
    }

    @RequiredArgsConstructor
    private static class AttendanceSummaryModel extends LoadableDetachableModel<AttendanceSummaryEntry> {

        private final IModel<Long> eventId;
        private final EventRecordRepository eventRecordRepository;

        @Override
        protected AttendanceSummaryEntry load() {
            Long id = eventId.getObject();
            AttendanceProjection projection = eventRecordRepository.findSummaryById(id)
                .orElseThrow(() -> new IllegalStateException("Event w/ id=" + id + " expected to be present"));
            return summarize(projection);
        }

        private AttendanceSummaryEntry summarize(AttendanceProjection event) {
            LocalDateTime eventStartLocal = event.getStartDateTime()
                .withZoneSameInstant(event.getStartZoneId())
                .toLocalDateTime();
            LocalDateTime eventEndLocal = event.getEndDateTime()
                .withZoneSameInstant(event.getEndZoneId())
                .toLocalDateTime();

            int attendeeCount = 0;
            int requiredBedCount = 0;
            int permanentCount = 0;
            int maxCount = 0;

            for (AttendanceProjection.Participation participation : event.getAttendees()) {
                InvitationStatus status = participation.getInvitationStatus();

                if (status == InvitationStatus.ACCEPTED
                    || status == InvitationStatus.TENTATIVE
                    || status == InvitationStatus.DECLINED) {
                    attendeeCount++;

                    if (participation.getAccommodationStatus() == AccommodationStatus.SEARCHING) {
                        requiredBedCount++;
                    }

                    if (status == InvitationStatus.ACCEPTED) {
                        maxCount++;

                        if (Objects.equals(participation.getFromDateTime(), eventStartLocal)
                            && Objects.equals(participation.getToDateTime(), eventEndLocal)) {
                            permanentCount++;
                        }
                    }
                }
            }

            return new AttendanceSummaryEntry(
                event.getId(),
                attendeeCount,
                requiredBedCount,
                permanentCount,
                maxCount
            );
        }
    }
}
