package de.vinado.wicket.bt4.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons.Type;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Vincent Nadoll
 */
public abstract class ConfirmationModal extends Modal<String> {

    private static final long serialVersionUID = 4302314305456195972L;

    public ConfirmationModal(ModalAnchor anchor, IModel<String> model) {
        super(anchor, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addCloseButton(new ResourceModel("abort", "Abort"));
        addAction(AjaxAction.create(new ResourceModel("confirm", "Confirm"), Type.Success, this::onConfirm));

        add(new SmartLinkMultiLineLabel("message", getModel())
            .setEscapeModelStrings(false));
    }

    protected abstract void onConfirm(AjaxRequestTarget target);
}
