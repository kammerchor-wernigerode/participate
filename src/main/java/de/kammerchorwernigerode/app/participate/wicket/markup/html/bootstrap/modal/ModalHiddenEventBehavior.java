package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal.Fullscreen;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal.Size;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;

public class ModalHiddenEventBehavior extends AjaxEventBehavior {

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

        resetComponents(modal);
        discard(modal, target);
    }

    @Override
    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
        super.updateAjaxAttributes(attributes);

        attributes.setEventPropagation(EventPropagation.BUBBLE);
    }

    private void resetComponents(Modal modal) {
        modal
            .staticBackdrop(false)
            .scrollable(false)
            .centered(false)
            .disableAnimation(false)
            .size(Size.DEFAULT)
            .fullscreen(Fullscreen.DEFAULT)
            .clearActions();
    }

    private void discard(Modal modal, AjaxRequestTarget target) {
        modal.setVisible(false);
        target.add(modal);
    }

    private Modal getModal() {
        return (Modal) getComponent();
    }
}
