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
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.Arrays;
import java.util.Date;

public abstract class DetailedParticipantFilterForm extends ParticipantFilterForm {

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterForm(String id, IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected void onInitialize() {
        add(comment("comment"));
        add(accommodation("accommodation"));

        DatetimePickerConfig toDateConfig = createDatetimePickerConfig();
        add(toDate("toDate", toDateConfig));
        add(fromDate("fromDate", toDateConfig::withMinDate));

        super.onInitialize();
    }

    protected MarkupContainer comment(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<String> model = LambdaModel.of(getModel(), ParticipantFilter::getComment, ParticipantFilter::setComment);
        FormComponent<String> control = new TextField<>("control", model)
            .setLabel(new ResourceModel("filter.comments", "Filter by comments"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer fromDate(String wicketId, SerializableConsumer<Date> onChange) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), ParticipantFilter::getFromDate, ParticipantFilter::setFromDate);
        DatetimePickerConfig config = createDatetimePickerConfig();
        FormComponent<Date> control = new DatetimePicker("control", model, config);
        control.setLabel(new ResourceModel("from", "From"));
        control.add(new DatetimePickerResettingBehavior(onChange));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer toDate(String wicketId, DatetimePickerConfig config) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), ParticipantFilter::getToDate, ParticipantFilter::setToDate);
        FormComponent<Date> control = new DatetimePicker("control", model, config);
        control.setLabel(new ResourceModel("to", "To"));
        control.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer accommodation(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Accommodation.Status> model = LambdaModel.of(getModel(), ParticipantFilter::getAccommodation, ParticipantFilter::setAccommodation);
        DropDownChoice<Accommodation.Status> control = new DropDownChoice<>("control", model,
            Arrays.asList(Accommodation.Status.values()), new EnumChoiceRenderer<>(this));
        control.setLabel(new ResourceModel("filter.participant.form.control.accommodation", "Accommodation"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
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
