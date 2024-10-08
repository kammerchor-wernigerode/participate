package de.vinado.wicket.participate.event.ui;

import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.event.presentation.ui.InvitationForm;
import de.vinado.app.participate.wicket.bt5.modal.Modal;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.DetailedParticipantColumnPreset;
import de.vinado.wicket.participate.ui.event.InteractiveColumnPresetDecoratorFactory;
import de.vinado.wicket.participate.ui.event.ParticipantTable;
import de.vinado.wicket.participate.ui.event.details.DetailedParticipantFilterPanel;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class EventSummaryListPanel extends BootstrapPanel<Event> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private final IModel<ParticipantFilter> filterModel;

    private final Modal modal;

    public EventSummaryListPanel(String id, IModel<Event> model,
                                 IModel<ParticipantFilter> filterModel, boolean editable) {
        super(id, model);

        this.filterModel = filterModel;
        this.modal = modal("modal");

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
            .build();

        ParticipantTable.Builder tableBuilder = ParticipantTable.builder("dataTable", dataProvider())
            .columns(decoratorFactory.decorate(baseColumns));
        add(tableBuilder.build());
    }

    protected Modal modal(String wicketId) {
        return new Modal(wicketId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(modal);
    }

    private ParticipantDataProvider dataProvider() {
        return new ParticipantDataProvider(getModel(), eventService, filterModel);
    }

    private void edit(AjaxRequestTarget target, IModel<Participant> rowModel) {
        ParticipantDTO participantDTO = new ParticipantDTO(rowModel.getObject());
        IModel<ParticipantDTO> model = new CompoundPropertyModel<>(participantDTO);

        modal
            .size(Modal.Size.LARGE)
            .title(new ResourceModel("invitation.edit", "Edit Invitation"))
            .content(id -> new InvitationForm(id, model))
            .addCloseAction(new ResourceModel("cancel", "Cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), this::onSave)
            .show(target);
    }

    private void onSave(AjaxRequestTarget target) {
        send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
        Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
    }

    @Override
    protected IModel<?> titleModel() {
        return getModel().map(EventName::of);
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
