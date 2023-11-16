package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.ui.event.ParticipantFilterPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public abstract class DetailedParticipantFilterPanel extends ParticipantFilterPanel {

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterPanel(String id, IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected WebMarkupContainer form(String id) {
        return new DetailedParticipantFilterForm(id, getModel(), eventModel) {
            @Override
            protected void onApply() {
                DetailedParticipantFilterPanel.this.onApply();
            }
        };
    }

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
