package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import org.apache.wicket.Component;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class ParticipantFilterPanel extends GenericPanel<ParticipantFilter> {

    public ParticipantFilterPanel(String id, IModel<ParticipantFilter> filterModel) {
        super(id, filterModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form("form"));
    }

    protected WebMarkupContainer form(String id) {
        return new ParticipantFilterForm(id, getModel()) {
            @Override
            protected void onApply() {
                ParticipantFilterPanel.this.onApply();
            }
        };
    }

    protected void onApply() {
        send(getScope(), Broadcast.BREADTH, new ParticipantFilterIntent(getModelObject()));
    }

    protected abstract Component getScope();
}
