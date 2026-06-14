package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

import lombok.Getter;

import static de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes.addClass;

public class ModalHeader extends Panel {

    @Getter
    private final Label label;

    public ModalHeader(String id) {
        this(id, null);
    }

    public ModalHeader(String id, IModel<?> model) {
        super(id, model);
        this.label = new Label("title", model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        label.setOutputMarkupId(true);

        add(label);
        add(new Modal.CloseButton("closeButton"));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        addClass(tag, "modal-header");
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (Strings.isEmpty(label.getDefaultModelObjectAsString())) {
            label.setDefaultModelObject("&nbsp;");
            label.setEscapeModelStrings(false);
        }
    }

    public ModalHeader title(IModel<?> model) {
        this.label.setDefaultModel(model);
        return this;
    }
}
