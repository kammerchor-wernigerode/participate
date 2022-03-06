package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.behavoirs.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import static de.vinado.wicket.participate.components.Models.map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventPanel extends BreadCrumbPanel implements IGenericComponent<EventDetails> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    private final PersonContext personContext;
    private final IModel<ParticipantFilter> filterModel;

    private final Form form;

    public EventPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventDetails> model, final boolean editable, PersonContext personContext, IModel<ParticipantFilter> filterModel) {
        super(id, breadCrumbModel, model);
        setOutputMarkupPlaceholderTag(true);

        this.personContext = personContext;
        this.filterModel = filterModel;

        form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        wmc.add(new UpdateOnEventBehavior<>(ParticipantFilterIntent.class));
        wmc.add(new UpdateOnEventBehavior<>(ParticipantTableUpdateIntent.class));
        form.add(wmc);

        wmc.add(new Label("name"));
        wmc.add(new Label("eventType"));
        wmc.add(new Label("displayDate"));
        wmc.add(new Label("creationDateTimeIso").add(new RelativeTimePipe()));
        wmc.add(new Label("location"));
        wmc.add(new MultiLineLabel("description") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isEmpty(model.getObject().getDescription()));
            }
        });

        final ParticipantFilterPanel filterPanel = new ParticipantFilterPanel("filterPanel", filterModel) {
            @Override
            protected Component getScope() {
                return wmc;
            }
        };
        wmc.add(filterPanel);

        BasicParticipantColumnPreset columns = new BasicParticipantColumnPreset();
        InteractiveColumnPresetDecoratorFactory decoratorFactory = InteractiveColumnPresetDecoratorFactory.builder()
            .visible(editable)
            .onEdit(EventPanel.this::edit)
            .onEmail(EventPanel.this::email)
            .build();

        ParticipantTable dataTable = ParticipantTable.builder("dataTable", dataProvider())
            .personContext(personContext)
            .rowsPerPage(15)
            .columns(decoratorFactory.decorate(columns))
            .build();
        wmc.add(dataTable);
    }

    private ParticipantDataProvider dataProvider() {
        return new ParticipantDataProvider(map(getModel(), EventDetails::getEvent), eventService, filterModel, personContext);
    }

    private void edit(AjaxRequestTarget target, IModel<Participant> rowModel) {
        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()))) {
            @Override
            protected void onSaveSubmit(final IModel<ParticipantDTO> savedModel, final AjaxRequestTarget target) {
                eventService.saveParticipant(savedModel.getObject());
                Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
            }
        });
        modal.show(target);
    }

    private void email(AjaxRequestTarget target, IModel<Participant> rowModel) {
        final Person person = rowModel.getObject().getSinger();
        Email mailData = emailBuilderFactory.create()
            .to(person)
            .build();

        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();
        if (payload instanceof EventUpdateEvent) {
            final EventUpdateEvent updateEvent = (EventUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            final Event event = updateEvent.getEvent();
            setModelObject(eventService.getEventDetails(event));
            target.add(form);
        }

        if (payload instanceof AjaxUpdateEvent) {
            final AjaxUpdateEvent event = (AjaxUpdateEvent) payload;
            final AjaxRequestTarget target = event.getTarget();
            target.add(form);
        }
    }

    @Override
    public IModel<String> getTitle() {
        return new PropertyModel<>(getModel(), "name");
    }

    @SuppressWarnings("unchecked")
    @Override
    public IModel<EventDetails> getModel() {
        return (IModel<EventDetails>) getDefaultModel();
    }

    @Override
    public void setModel(IModel<EventDetails> model) {
        setDefaultModel(model);
    }

    @Override
    public void setModelObject(EventDetails object) {
        setDefaultModelObject(object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventDetails getModelObject() {
        return (EventDetails) getDefaultModelObject();
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
