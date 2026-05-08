package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;

public abstract class ModalHiddenEventBehavior extends AjaxEventBehavior {

    public ModalHiddenEventBehavior() {
        super("hidden.bs.modal");
    }

    @Override
    protected void onBind() {
        super.onBind();

        if (getComponent() instanceof Modal) {
            return;
        }

        throw new WicketRuntimeException("ModalHiddenEventBehavior.onBind() called with an invalid component.");
    }

    @Override
    protected void onEvent(AjaxRequestTarget target) {
        Modal modal = getModal();
        if (!modal.isVisibleInHierarchy()) {
            return;
        }

        discard(modal, target);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        attributes.setEventPropagation(EventPropagation.BUBBLE);
    }

    private void discard(Modal modal, AjaxRequestTarget target) {
        modal.setVisible(false);
        target.add(modal);
    }

    protected Modal getModal() {
        return (Modal) getComponent();
    }
}
