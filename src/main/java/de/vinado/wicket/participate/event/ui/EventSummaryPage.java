package de.vinado.wicket.participate.event.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.INamedParameters.Type;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.Date;
import java.util.Optional;

import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Default;
import static de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type.Link;

public class EventSummaryPage extends ParticipatePage implements IGenericComponent<EventDetails, EventSummaryPage> {

    @SpringBean
    private EventService eventService;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        EventDetails eventDetails = Optional.ofNullable(getPageParameters().get("event"))
            .map(StringValue::toOptionalLong)
            .flatMap(eventService::findById)
            .or(() -> Optional.ofNullable(eventService.getLatestEventDetails()))
            .orElseThrow(IllegalArgumentException::new);
        setModel(CompoundPropertyModel.of(eventDetails));

        add(new Label("eventName", getModel().map(EventName::of)));

        BootstrapAjaxLink<EventDetails> previousEventBtn = new BootstrapAjaxLink<>("previousEventBtn", getModel(), Link) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Optional.ofNullable(eventService.getPredecessor(getModelObject()))
                    .ifPresent(EventSummaryPage.this::navigate);
            }
        };
        previousEventBtn.setOutputMarkupPlaceholderTag(true);
        previousEventBtn.setSize(Buttons.Size.Small);
        previousEventBtn.setIconType(FontAwesome6IconType.caret_left_s);
        add(previousEventBtn);

        BootstrapBookmarkablePageLink<Void> backBtn = new BootstrapBookmarkablePageLink<>("backBtn", EventsPage.class, Default);
        backBtn.setLabel(new ResourceModel("show.event.overview", "Show Event Overview"));
        backBtn.setSize(Buttons.Size.Small);
        backBtn.setIconType(FontAwesome6IconType.calendar_s);
        add(backBtn);

        BootstrapAjaxLink<EventDetails> nextEventBtn = new BootstrapAjaxLink<>("nextEventBtn", getModel(), Link) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Optional.ofNullable(eventService.getSuccessor(getModelObject()))
                    .ifPresent(EventSummaryPage.this::navigate);
            }
        };
        nextEventBtn.setOutputMarkupPlaceholderTag(true);
        nextEventBtn.setSize(Buttons.Size.Small);
        nextEventBtn.setIconType(FontAwesome6IconType.caret_right_s);
        add(nextEventBtn);

        add(new Label("acceptedCount"));
        add(new Label("declinedCount"));
        add(new Label("pendingCount"));
        add(new Label("tentativeCount"));
        add(new Label("sopranoCount"));
        add(new Label("altoCount"));
        add(new Label("tenorCount"));
        add(new Label("bassCount"));
        add(accommodationDemand("accommodationDemand"));
        add(accommodationSupply("accommodationSupply"));
        add(new Label("sopranos"));
        add(new Label("altos"));
        add(new Label("tenors"));
        add(new Label("basses"));
        add(new Label("tentative"));
        add(new Label("declined"));
        add(new Label("carCount"));
        add(new Label("carSeatCount"));
        add(new Label("carSingerCount", new PropertyModel<>(getModel(), "acceptedSum")));

        // Unterer Bereich
        IModel<ParticipantFilter> filterModel = new CompoundPropertyModel<>(new ParticipantFilter());
        EventSummaryListPanel listPanel = new EventSummaryListPanel("listPanel", getModel().map(EventDetails::getEvent),
            filterModel, getModelObject().getEndDate().after(new Date()));
        listPanel.add(new UpdateOnEventBehavior<>(ParticipantFilterIntent.class));
        listPanel.add(new UpdateOnEventBehavior<>(ParticipantTableUpdateIntent.class));
        listPanel.setOutputMarkupId(true);
        add(listPanel);
    }

    protected Component accommodationDemand(String wicketId) {
        IModel<?> model = new StringResourceModel("event.participant.accommodation.demand", getModel());
        return new Label(wicketId, model);
    }

    protected Component accommodationSupply(String wicketId) {
        IModel<?> model = new StringResourceModel("event.participant.accommodation.supply", getModel());
        return new Label(wicketId, model);
    }

    private void navigate(EventDetails event) {
        getSession().setMetaData(ManagementSession.event, event.getEvent());
        PageParameters pageParameters = new PageParameters(getPageParameters());
        pageParameters.set("event", event.getId(), Type.PATH);
        setResponsePage(EventSummaryPage.class, pageParameters);
    }
}
