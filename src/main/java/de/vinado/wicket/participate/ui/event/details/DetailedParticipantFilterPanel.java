package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import org.apache.wicket.Component;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class DetailedParticipantFilterPanel extends GenericPanel<ParticipantFilter> {

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterPanel(String id,
                                          IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form("form"));
    }

    private WebMarkupContainer form(String id) {
        return new DetailedParticipantFilterForm(id, getModel(), eventModel) {
            @Override
            protected void onApply() {
                DetailedParticipantFilterPanel.this.onApply();
            }
        };
    }

    private void onApply() {
        send(getScope(), Broadcast.BREADTH, new ParticipantFilterIntent(getModelObject()));
    }

    protected abstract Component getScope();

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
