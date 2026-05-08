package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal.Fullscreen;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal.Size;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ResetModalBehavior extends ModalHiddenEventBehavior {

    @Override
    protected void onEvent(AjaxRequestTarget target) {
        super.onEvent(target);

        Modal modal = getModal();
        resetComponents(modal);
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
}
