package de.vinado.wicket.participate.components.modals;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;


/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Deprecated
public abstract class BootstrapModalConfirmationPanel extends BootstrapModalPanel<String> {

    /**
     * @param modal   {@link BootstrapModal}
     * @param title   Title
     * @param message Message of the display
     */
    public BootstrapModalConfirmationPanel(final BootstrapModal modal, final IModel<String> title,
                                           final IModel<String> message) {
        super(modal, title, message);

        final Label confirmMessage = new Label("message", message);
        confirmMessage.setEscapeModelStrings(false);
        inner.add(confirmMessage);
    }

    /**
     * @param text   Text of the confirmation panel.
     * @param target {@link org.apache.wicket.ajax.AjaxRequestTarget}
     */
    @Override
    protected void onSaveSubmit(IModel<String> text, AjaxRequestTarget target) {
        onConfirm(target);
    }

    protected abstract void onConfirm(final AjaxRequestTarget target);
}
