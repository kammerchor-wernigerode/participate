package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.DetailedParticipantColumnPreset;
import de.vinado.wicket.participate.ui.event.EditInvitationPanel;
import de.vinado.wicket.participate.ui.event.InteractiveColumnPresetDecoratorFactory;
import de.vinado.wicket.participate.ui.event.ParticipantTable;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryListPanel extends GenericPanel<Event> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    private final IModel<ParticipantFilter> filterModel;

    public EventSummaryListPanel(String id, IModel<Event> model,
                                 IModel<ParticipantFilter> filterModel, boolean editable) {
        super(id, model);

        this.filterModel = filterModel;

        DetailedParticipantFilterPanel filterPanel = new DetailedParticipantFilterPanel("filterPanel",
            filterModel, model) {
            @Override
            protected Component getScope() {
                return EventSummaryListPanel.this;
            }
        };
        add(filterPanel);

        DetailedParticipantColumnPreset baseColumns = new DetailedParticipantColumnPreset();
        InteractiveColumnPresetDecoratorFactory decoratorFactory = InteractiveColumnPresetDecoratorFactory.builder()
            .visible(editable)
            .onEdit(EventSummaryListPanel.this::edit)
            .onEmail(EventSummaryListPanel.this::email)
            .build();

        ParticipantTable.Builder tableBuilder = ParticipantTable.builder("dataTable", dataProvider())
            .columns(decoratorFactory.decorate(baseColumns));
        add(tableBuilder.build());
    }

    private ParticipantDataProvider dataProvider() {
        return new ParticipantDataProvider(getModel(), eventService, filterModel);
    }

    private void edit(AjaxRequestTarget target, IModel<Participant> rowModel) {
        BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        ParticipantDTO participantDTO = new ParticipantDTO(rowModel.getObject());

        modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(participantDTO)) {
            @Override
            protected void onSaveSubmit(IModel<ParticipantDTO> savedModel,
                                        AjaxRequestTarget target) {
                eventService.saveParticipant(savedModel.getObject());
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
                Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
            }
        });
        modal.show(target);
    }

    private void email(AjaxRequestTarget target, IModel<Participant> rowModel) {
        Person person = rowModel.getObject().getSinger();
        Email mailData = emailBuilderFactory.create()
            .to(person)
            .build();

        BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
