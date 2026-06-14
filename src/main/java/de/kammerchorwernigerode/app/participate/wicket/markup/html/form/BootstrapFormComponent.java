package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.panel.Feedback;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.FeedbackMessages;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.danekja.java.util.function.serializable.SerializableFunction;

import lombok.Getter;

public abstract class BootstrapFormComponent<T, R> extends FormComponentPanel<R> {

    @Getter
    private final FormComponent<T> formComponent;
    private final WebMarkupContainer container;
    private final InputAdornment endAdornment;
    private final Feedback feedback;

    @Getter
    private Layout layout = Layout.DEFAULT;

    public BootstrapFormComponent(String id, IModel<R> model) {
        super(id, model);
        this.container = new WebMarkupContainer("container");
        this.formComponent = createFormComponent("control");
        this.endAdornment = new InputAdornment("endAdornment");
        this.feedback = new Feedback("feedback", this);
    }

    protected abstract FormComponent<T> createFormComponent(String wicketId);

    public BootstrapFormComponent<T, R> setLayout(Layout layout) {
        this.layout = layout;
        return this;
    }

    public BootstrapFormComponent<T, R> addEndAdornment(SerializableFunction<String, Component> constructor) {
        endAdornment.addChild(constructor);
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Fragment fragment;
        switch (layout) {
            case DEFAULT:
                fragment = new Fragment("layout", "default", this);
                break;
            case FLOATING_LABEL:
                fragment = new Fragment("layout", "floatingLabel", this);
                formComponent.add(AttributeModifier.replace("placeholder", getLabel()));
                add(ClassAttributeModifier.append("class", "form-floating"));
                break;
            default:
                throw new WicketRuntimeException("Unsupported layout" + layout);
        }
        fragment.add(container);
        add(fragment);

        container.add(endAdornment);

        formComponent.setModel(new CompoundPropertyModel<>(null));
        formComponent.add(ClassAttributeModifier.append("class", getCssClassName()));
        container.add(formComponent);

        FormLabel label = new FormLabel("label", this)
            .references(formComponent);
        fragment.add(label);

        feedback.setOutputMarkupId(true);
        add(feedback);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        container.setRenderBodyOnly(true);
        if (Layout.FLOATING_LABEL.equals(layout)) {
            return;
        }

        if (endAdornment.isVisible()) {
            container.setRenderBodyOnly(false);
            container.add(ClassAttributeModifier.append("class", "input-group"));
        }
    }

    @Override
    public abstract void convertInput();

    @Override
    protected void onValid() {
        String cssClassName = getCssClassName();
        formComponent.add(ClassAttributeModifier.replace("class", cssClassName));
        updateFeedback();
    }

    @Override
    protected void onInvalid() {
        FeedbackMessages messages = getFeedbackMessages();
        ErrorMessageFilter filter = new ErrorMessageFilter(this);

        String cssClass = getCssClassName();
        if (messages.hasMessage(filter)) {
            cssClass += " is-invalid";
        } else {
            cssClass += " is-valid";
        }
        formComponent.add(ClassAttributeModifier.replace("class", cssClass));

        updateFeedback();
    }

    protected abstract String getCssClassName();

    protected void updateFeedback() {
        formComponent.add(AttributeModifier.replace("aria-describedby", feedback.getMarkupId()));
        RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(feedback));
    }


    public enum Layout {

        DEFAULT,
        FLOATING_LABEL,
        ;
    }
}
