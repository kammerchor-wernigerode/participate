package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry_;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventSelected;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details.EventPanel;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.table.EventTablePanel;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import de.kammerchorwernigerode.app.participate.wicket.behavior.OnEventBehavior;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketSession;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

public class EventsPage extends ParticipatePage {

    @SpringBean
    private EventEntryRepository eventEntryRepository;

    private Panel eventPanel;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        EventEntrySpecification eventSpecification = new EventEntrySpecification(this::minInstant);
        IModel<EventEntrySpecification> specModel = new CompoundPropertyModel<>(eventSpecification);
        EventTablePanel eventTablePanel = new EventTablePanel("events", specModel);
        eventTablePanel.setOutputMarkupId(true);
        add(eventTablePanel);


        EventModel model = new EventModel(eventEntryRepository, eventSpecification);
        eventPanel = createEventPanel(model);
        add(eventPanel);
    }

    private Instant minInstant() {
        RequestCycle requestCycle = RequestCycle.get();
        long startTime = requestCycle.getStartTime();
        ZoneId zone = ZoneId.systemDefault();
        return Instant.ofEpochMilli(startTime).atZone(zone)
            .withDayOfYear(1)
            .toLocalDate()
            .atStartOfDay(zone)
            .toInstant();
    }

    private Panel createEventPanel(EventModel model) {
        Panel panel = null == model.getObject()
            ? new EmptyPanel("event")
            : new EventPanel("event", model);
        panel.add(new ReplaceOnEventBehavior(model));
        return panel;
    }

    @Override
    protected IModel<?> titleModel() {
        return new ResourceModel("events");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(EventsCssResourceReference.asHeaderItem());
    }


    private class ReplaceOnEventBehavior extends OnEventBehavior<EventSelected> {

        private final EventModel model;

        public ReplaceOnEventBehavior(EventModel model) {
            super(EventSelected.class);
            this.model = model;
        }

        @Override
        protected void onEvent(Component component, EventSelected payload) {
            Long eventId = payload.eventId();
            Session session = Session.get();
            session.setMetaData(ManagementWicketSession.selectedEventId, eventId);

            Panel replacement = createEventPanel(model);
            eventPanel.replaceWith(replacement);
            eventPanel = replacement;

            RequestCycle.get().find(AjaxRequestTarget.class)
                .ifPresent(target -> target.add(eventPanel));
        }
    }


    @RequiredArgsConstructor
    private static class EventModel extends LoadableDetachableModel<EventEntry> {

        private final EventEntryRepository eventEntryRepository;
        private final Specification<EventEntry> specification;

        @Override
        protected EventEntry load() {
            Session session = Session.get();
            Long eventId = session.getMetaData(ManagementWicketSession.selectedEventId);
            return Optional.ofNullable(eventId)
                .flatMap(eventEntryRepository::findById)
                .or(this::findFirst)
                .orElse(null);
        }

        private Optional<EventEntry> findFirst() {
            Sort sort = JpaSort.of(Direction.ASC, EventEntry_.startInstant);
            Pageable pageable = PageRequest.of(0, 1, sort);

            return eventEntryRepository.findAll(specification, pageable).stream()
                .findFirst();
        }
    }
}
