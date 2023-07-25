package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerIconConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResetIntent;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResettingBehavior;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.ui.event.ParticipantFilterForm;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Arrays;
import java.util.Date;

/**
 * @author Vincent Nadoll
 */
public abstract class DetailedParticipantFilterForm extends ParticipantFilterForm {

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterForm(String id, IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected void onInitialize() {
        add(commentInput("comment"));
        add(accommodationInput("accommodation"));
        add(cateringInput("catering"));

        DatetimePickerConfig toDateConfig = createDatetimePickerConfig();
        add(toDateInput("toDate", toDateConfig));
        add(fromDateInput("fromDate", toDateConfig::withMinDate));

        super.onInitialize();
    }

    protected FormComponent<String> commentInput(String id) {
        TextField<String> field = new TextField<>(id);
        field.setLabel(new ResourceModel("filter.comments", "Filter by comments"));
        return field;
    }

    protected FormComponent<Date> fromDateInput(String id, SerializableConsumer<Date> onChange) {
        DatetimePickerConfig config = createDatetimePickerConfig();

        DatetimePicker field = new DatetimePicker(id, config);
        field.setLabel(new ResourceModel("from", "From"));
        field.add(new DatetimePickerResettingBehavior(onChange));
        return field;
    }

    protected FormComponent<Date> toDateInput(String id, DatetimePickerConfig config) {
        DatetimePicker field = new DatetimePicker(id, config);
        field.setLabel(new ResourceModel("to", "To"));
        field.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        return field;
    }

    protected FormComponent<Accommodation.Status> accommodationInput(String id) {
        DropDownChoice<Accommodation.Status> select = new DropDownChoice<>(id,
            Arrays.asList(Accommodation.Status.values()), new EnumChoiceRenderer<>(this));
        select.setLabel(new ResourceModel("filter.participant.form.control.accommodation", "Accommodation"));
        return select;
    }

    protected FormComponent<Boolean> cateringInput(String id) {
        return new CheckBox(id);
    }

    protected DatetimePickerConfig createDatetimePickerConfig() {
        Event event = eventModel.getObject();
        DatetimePickerConfig config = new DatetimePickerConfig();
        config.with(new DatetimePickerIconConfig());
        config.useLocale(getLocale().getLanguage());
        config.withMinDate(event.getStartDate());
        config.withMaxDate(DateUtils.addMilliseconds(DateUtils.addDays(event.getEndDate(), 1), -1));
        config.withFormat("dd.MM.yyyy HH:mm");
        config.withMinuteStepping(30);
        config.useCurrent(false);
        return config;
    }

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
