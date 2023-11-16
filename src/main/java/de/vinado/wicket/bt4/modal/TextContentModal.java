package de.vinado.wicket.bt4.modal;

import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class TextContentModal extends Modal<String> {

    public TextContentModal(ModalAnchor anchor, IModel<String> model) {
        super(anchor, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addCloseButton(new ResourceModel("close", "Close"));

        add(new SmartLinkMultiLineLabel("message", getModel())
            .setEscapeModelStrings(false));
    }
}
