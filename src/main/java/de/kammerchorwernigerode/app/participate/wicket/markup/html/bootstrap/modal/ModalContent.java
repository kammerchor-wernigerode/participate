package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalFooter.Action;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import static de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes.addClass;

public class ModalContent extends Border {

    public static final String ACTION_WICKET_ID = "action";

    private final ModalHeader header;
    private final ModalFooter footer;

    public ModalContent(String id) {
        super(id);
        this.header = new ModalHeader("header", Model.of());
        this.footer = new ModalFooter("footer");
    }

    public final String getActionId() {
        return ACTION_WICKET_ID;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addToBorder(header, footer);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        addClass(tag, "modal-content");
    }

    public Component getLabel() {
        return this.header.getLabel();
    }

    public ModalContent title(IModel<?> title) {
        this.header.title(title);
        return this;
    }

    public ModalContent addSubmitAction(IModel<?> label) {
        this.footer.addSubmitAction(label);
        return this;
    }

    public ModalContent addSubmitAction(IModel<?> label, SerializableConsumer<AjaxRequestTarget> onAfterSubmit) {
        this.footer.addSubmitAction(label, onAfterSubmit);
        return this;
    }

    public ModalContent addCloseAction(IModel<?> label) {
        this.footer.addCloseAction(label);
        return this;
    }

    public ModalContent addAction(SerializableFunction<String, Action> constructor) {
        this.footer.addAction(constructor);
        return this;
    }

    public ModalContent addAction(Action action) {
        footer.addAction(action);
        return this;
    }
}
