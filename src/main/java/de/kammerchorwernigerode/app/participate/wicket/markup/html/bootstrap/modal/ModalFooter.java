package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.BootstrapButton;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.ButtonBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.Icon;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.ComponentListView;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes.addClass;

public class ModalFooter extends Panel {

    public static final String ACTION_WICKET_ID = "action";

    private final List<Component> actions = new LinkedList<>();

    public ModalFooter(String id) {
        super(id);
    }

    public final String getActionId() {
        return ACTION_WICKET_ID;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ComponentListView("actions", actions));
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        addClass(tag, "modal-footer");
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        setVisible(!actions.isEmpty());
    }

    @Override
    public ModalFooter add(Component... children) {
        for (Component component : children) {
            if (component instanceof Action action) {
                addAction(action);
            } else {
                super.add(component);
            }
        }
        return this;
    }

    public ModalFooter addSubmitAction(IModel<?> label) {
        return addSubmitAction(label, ignored -> { });
    }

    public ModalFooter addSubmitAction(IModel<?> label, SerializableConsumer<AjaxRequestTarget> onAfterSubmit) {
        return addAction(id -> new SubmitAction(id, label) {

            @Override
            protected void onAfterSubmit(AjaxRequestTarget target) {
                super.onAfterSubmit(target);
                onAfterSubmit.accept(target);
            }
        });
    }

    public ModalFooter addCloseAction(IModel<?> label) {
        return addAction(id -> new CloseAction(id, label));
    }

    public ModalFooter addAction(SerializableFunction<String, Action> constructor) {
        Action button = constructor.apply(ACTION_WICKET_ID);
        return addAction(button);
    }

    public ModalFooter addAction(Action action) {
        if (!ACTION_WICKET_ID.equals(action.getId())) {
            throw new IllegalArgumentException("Invalid action Wicket ID. Must be '" + ACTION_WICKET_ID + "'.");
        }

        actions.add(action);
        return this;
    }


    public abstract static class Action extends WebMarkupContainer implements BootstrapButton<Action> {

        private final Icon icon;
        private final IModel<?> label;
        private final ButtonBehavior buttonBehavior = new ButtonBehavior();

        public Action(String id, IModel<?> label) {
            super(id);
            this.icon = new Icon("icon", (IconType) null);
            this.label = label;
        }

        @Override
        public Action setVariant(Buttons.Variant variant) {
            buttonBehavior.setVariant(variant);
            return this;
        }

        @Override
        public Action setSize(Buttons.Size size) {
            buttonBehavior.setSize(size);
            return this;
        }

        public Action setIcon(IconType icon) {
            this.icon.setType(icon);
            return this;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            AbstractLink button = createButton("button");
            configure(button);
            button.add(buttonBehavior);
            add(button);

            button.add(icon);

            Label body = new Label("label", label);
            body.setRenderBodyOnly(true);
            button.add(body);
        }

        @Override
        protected void onDetach() {
            super.onDetach();
            label.detach();
        }

        protected abstract AbstractLink createButton(String wicketId);

        protected void configure(AbstractLink button) {
            button.add(new AttributeModifier("type", "button"));
        }
    }

    public static class CloseAction extends Action {

        public CloseAction(String id, IModel<?> label) {
            super(id, label);
        }

        @Override
        protected AbstractLink createButton(String wicketId) {
            return new Modal.CloseButton(wicketId);
        }
    }

    public static class SubmitAction extends Action {

        public SubmitAction(String id, IModel<?> label) {
            super(id, label);
            setVariant(Buttons.Variant.PRIMARY);
        }

        @Override
        protected AbstractLink createButton(String wicketId) {
            Form<?> form = form().orElse(null);
            return new AjaxSubmitLink(wicketId, form) {

                @Override
                protected void onAfterSubmit(AjaxRequestTarget target) {
                    SubmitAction.this.onAfterSubmit(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target) {
                    SubmitAction.this.onError(target);
                }
            };
        }

        @Override
        protected void configure(AbstractLink link) {
            super.configure(link);

            form().ifPresent(form -> {
                link.add(new AttributeModifier("type", "submit"));
                link.add(new AttributeModifier("form", form.getMarkupId(true)));
            });
        }

        @SuppressWarnings("rawtypes")
        private Optional<Form> form() {
            Modal modal = findParent(Modal.class);
            return modal.streamChildren()
                .filter(Form.class::isInstance)
                .map(Form.class::cast)
                .findFirst();
        }

        protected void onAfterSubmit(AjaxRequestTarget target) {
            Modal modal = findParent(Modal.class);
            modal.appendHideDialogJavaScript(target);
        }

        protected void onError(AjaxRequestTarget target) {
        }
    }
}
