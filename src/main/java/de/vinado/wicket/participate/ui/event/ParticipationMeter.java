package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.EventDetails;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class ParticipationMeter extends GenericPanel<EventDetails> {

    public ParticipationMeter(String id, IModel<EventDetails> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(accepted("a"));
        add(declined("d"));
        add(pending("p"));
    }

    private Component accepted(String wicketId) {
        return new Section(wicketId, acceptedModel());
    }

    private IModel<Long> acceptedModel() {
        return getModel().map(EventDetails::getAcceptedSum);
    }

    private Component declined(String wicketId) {
        return new Section(wicketId, declinedModel());
    }

    private IModel<Long> declinedModel() {
        return getModel().map(EventDetails::getDeclinedCount);
    }

    private Component pending(String wicketId) {
        return new Section(wicketId, pendingModel());
    }

    private IModel<Long> pendingModel() {
        return getModel().map(EventDetails::getPendingCount);
    }


    private static class Section extends Label implements IGenericComponent<Long, Section> {

        public Section(String id, IModel<Long> model) {
            super(id, model);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(AttributeModifier.append("class", getId()));
        }
    }
}
