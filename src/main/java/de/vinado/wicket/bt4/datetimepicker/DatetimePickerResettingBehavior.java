package de.vinado.wicket.bt4.datetimepicker;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Date;

/**
 * @author Vincent Nadoll
 */
public class DatetimePickerResettingBehavior extends AjaxFormComponentUpdatingBehavior {

    private final SerializableConsumer<Date> currentValue;

    public DatetimePickerResettingBehavior(SerializableConsumer<Date> currentValue) {
        super("change.datetimepicker");
        this.currentValue = currentValue;
    }

    @Override
    protected void onBind() {
        super.onBind();
        FormComponent<?> component = getFormComponent();
        if (!(component instanceof DatetimePicker)) {
            throw new WicketRuntimeException("Behavior " + getClass().getName()
                + " can only be added to an instance of a DatetimePicker");
        }
    }

    @Override
    protected void onUpdate(AjaxRequestTarget target) {
        DatetimePicker component = (DatetimePicker) getFormComponent();
        String value = component.getValue();
        if (Strings.isEmpty(value)) {
            return;
        }

        currentValue.accept(component.getConvertedInput());
        onReset(target);
    }

    protected void onReset(AjaxRequestTarget target) {
        getComponent().send(getScope(), Broadcast.BREADTH, new DatetimePickerResetIntent());
    }

    protected IEventSink getScope() {
        return getFormComponent().getForm();
    }
}
