package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalFooter.Action;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ModalFooter.SubmitAction;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import lombok.Getter;

public class ModalBody extends Panel {

    @Getter
    private final ModalContent dialogContent;

    public ModalBody(String id) {
        this(id, null);
    }

    public ModalBody(String id, IModel<?> model) {
        super(id, model);
        this.dialogContent = new ModalContent("dialog");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(dialogContent);
    }

    @Override
    public MarkupContainer add(Component... children) {
        for (Component component : children) {
            if (component == dialogContent || component.isAuto()) {
                super.add(component);
            } else {
                addToDialogContent(component);
            }
        }
        return this;
    }

    @Override
    public MarkupContainer addOrReplace(Component... children) {
        for (Component component : children) {
            if (component == dialogContent) {
                super.addOrReplace(component);
            } else {
                dialogContent.addOrReplace(component);
            }
        }
        return this;
    }

    public ModalBody addToDialogContent(Component... components) {
        dialogContent.add(components);
        return this;
    }

    public ModalBody title(IModel<?> title) {
        this.dialogContent.title(title);
        return this;
    }

    public ModalBody addSubmitAction(IModel<?> label) {
        this.dialogContent.addAction(id -> new SubmitAction(id, label));
        return this;
    }

    public ModalBody addSubmitAction(IModel<?> label, SerializableConsumer<AjaxRequestTarget> onAfterSubmit) {
        this.dialogContent.addSubmitAction(label, onAfterSubmit);
        return this;
    }

    public ModalBody addCloseAction(IModel<?> label) {
        this.dialogContent.addCloseAction(label);
        return this;
    }

    public ModalBody addAction(SerializableFunction<String, Action> constructor) {
        this.dialogContent.addAction(constructor);
        return this;
    }

    public ModalBody addAction(Action action) {
        this.dialogContent.addAction(action);
        return this;
    }
}
