package de.vinado.wicket.bt4.modal;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.bt4.form.decorator.BootstrapFormDecorator;
import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.form.decorator.BootstrapInlineFormDecorator;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public abstract class FormModal<T> extends Modal<T> {

    private static final long serialVersionUID = -1686181807165719605L;

    protected static final String FORM_ID = "form";
    protected static final String FEEDBACK_ID = "feedback";

    protected Form<T> form;
    protected NotificationPanel feedback;

    public FormModal(ModalAnchor anchor, IModel<T> model) {
        super(anchor, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form = createForm(FORM_ID));
        form.setOutputMarkupId(true);
        setupActions();

        form.add(feedback = createFeedback(FEEDBACK_ID));
        feedback.setOutputMarkupId(true);
    }

    protected Form<T> createForm(String id) {
        return new Form<>(id, getModel());
    }

    protected NotificationPanel createFeedback(String id) {
        return new NotificationPanel(id);
    }

    protected void setupActions() {
        addCloseButton(abortButtonLabel());
        addAction(AjaxSubmitAction.create(submitButtonLabel(), form, this::onSubmit));
    }

    protected IModel<String> abortButtonLabel() {
        return new ResourceModel("abort", "Abort");
    }

    protected IModel<String> submitButtonLabel() {
        return new ResourceModel("save", "Save");
    }

    protected abstract void onSubmit(AjaxRequestTarget target);

    protected void addBootstrapFormDecorator(Form<T> form) {
        form.visitChildren(FormComponent.class, (component, visit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }

    protected void addBootstrapInlineFormDecorator(Form<T> form) {
        form.add(new AttributeModifier("class", "form-inline"));
        form.visitChildren(FormComponent.class, (component, visit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }

    protected void addBootstrapHorizontalFormDecorator(Form<T> form) {
        form.add(new AttributeModifier("class", "form-horizontal"));
        form.visitChildren(FormComponent.class, (component, visit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapHorizontalFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });
    }


    public static abstract class AjaxSubmitAction<T> extends AbstractAction {

        private static final long serialVersionUID = -8731687167073501915L;

        private final Form<T> form;

        @Getter(AccessLevel.PROTECTED)
        private final ButtonBehavior buttonBehavior;

        public AjaxSubmitAction(String id, IModel<String> labelModel, Form<T> form) {
            super(id, labelModel);
            this.form = form;
            this.buttonBehavior = new ButtonBehavior(Buttons.Type.Primary);
        }

        public static <T> SerializableFunction<String, AbstractAction> create(IModel<String> labelModel, Form<T> form,
                                                                              SerializableConsumer<AjaxRequestTarget> clickHandler) {
            return id -> new AjaxSubmitAction<>(id, labelModel, form) {
                private static final long serialVersionUID = -8512147121981227699L;

                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    clickHandler.accept(target);
                    Optional.ofNullable(findParent(ModalAnchor.class))
                        .ifPresent(anchor -> anchor.close(target));
                }
            };
        }

        @Override
        protected AbstractLink link(String id) {
            AjaxSubmitLink link = new AjaxSubmitLink(id, form) {
                private static final long serialVersionUID = -96187706316648756L;

                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(buttonBehavior);
                }

                @Override
                protected void onError(AjaxRequestTarget target) {
                    super.onError(target);

                    Optional.ofNullable(getForm().get(FEEDBACK_ID))
                        .ifPresent(feedback -> onError(target, feedback));
                }

                private void onError(AjaxRequestTarget target, Component feedback) {
                    target.add(feedback);
                    getForm().visitFormComponents((components, iVisit) -> {
                        if (!components.getRenderBodyOnly()) target.add(components);
                    });
                }

                @Override
                protected void onSubmit(AjaxRequestTarget target) {
                    super.onSubmit(target);
                    AjaxSubmitAction.this.onSubmit(target);
                }
            };
            link.add(new AttributeModifier("form", form.getMarkupId()));
            return link;
        }

        protected abstract void onSubmit(AjaxRequestTarget target);
    }
}
