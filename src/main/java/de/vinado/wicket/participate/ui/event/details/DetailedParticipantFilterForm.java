package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.ui.event.ParticipantFilterForm;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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

        DateTextFieldConfig toDateConfig = createDateTextFieldConfig();
        MarkupContainer toDateField = add(toDateInput("toDate", toDateConfig));
        toDateField.setOutputMarkupId(true);
        add(fromDateInput("fromDate", (value, target) -> {
            toDateConfig.withStartDate(DateTime.parse(value, DateTimeFormat.forPattern("dd.MM.yyyy")));
            target.add(toDateField);
        }));

        super.onInitialize();
    }

    protected FormComponent<String> commentInput(String id) {
        TextField<String> field = new TextField<>(id);
        field.setLabel(new ResourceModel("filter.comments", "Filter by comments"));
        return field;
    }

    protected FormComponent<Date> fromDateInput(String id, SerializableBiConsumer<String, AjaxRequestTarget> onChange) {
        DateTextFieldConfig config = createDateTextFieldConfig();

        DateTextField field = new DateTextField(id, config);
        field.setLabel(new ResourceModel("from", "From"));
        field.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                onChange.accept(getFormComponent().getValue(), target);
            }
        });
        return field;
    }

    protected FormComponent<Date> toDateInput(String id, DateTextFieldConfig config) {
        FormComponent<Date> field = new DateTextField(id, config);
        field.setLabel(new ResourceModel("to", "To"));
        field.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        return field;
    }

    protected FormComponent<Boolean> accommodationInput(String id) {
        return new CheckBox(id);
    }

    protected FormComponent<Boolean> cateringInput(String id) {
        return new CheckBox(id);
    }

    protected DateTextFieldConfig createDateTextFieldConfig() {
        Event event = eventModel.getObject();
        DateTextFieldConfig config = new DateTextFieldConfig();
        config.withLanguage("de");
        config.withFormat("dd.MM.yyyy");
        config.withStartDate(new DateTime(event.getStartDate()));
        config.withEndDate(new DateTime(event.getEndDate()));
        config.autoClose(true);
        return config;
    }

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
