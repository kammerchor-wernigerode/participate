package de.vinado.app.participate.wicket.form;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Date;

public class DateTextFieldResettingBehavior extends AjaxFormComponentUpdatingBehavior {

    private final SerializableConsumer<Date> currentValue;

    public DateTextFieldResettingBehavior(SerializableConsumer<Date> currentValue) {
        super("change.datetimepicker");
        this.currentValue = currentValue;
    }

    @Override
    protected void onBind() {
        super.onBind();
        FormComponent<?> component = getFormComponent();
        if (!(component.getType().isAssignableFrom(Date.class))) {
            throw new WicketRuntimeException("Behavior " + getClass().getName()
                + " can only be added to an instance of FormComponent");
        }
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        FormComponent<Date> component = (FormComponent<Date>) getFormComponent();
        String value = component.getValue();
        if (Strings.isEmpty(value)) {
            return;
        }

        currentValue.accept(component.getConvertedInput());
        onReset(target);
    }

    protected void onReset(AjaxRequestTarget target) {
        getComponent().send(getScope(), Broadcast.BREADTH, new DateTextFieldResetIntent());
    }

    protected IEventSink getScope() {
        return getFormComponent().getForm();
    }
}
