package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.model.DateRange;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.datetime.DateTimeLocalTextField;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.AbstractFilter;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;

import java.time.LocalDateTime;

public class DateRangeFilter extends AbstractFilter implements IGenericComponent<DateRange, DateRangeFilter> {

    private final FilterForm<?> form;

    public DateRangeFilter(String id, IModel<DateRange> model, FilterForm<?> form) {
        super(id, form);
        this.form = form;

        setModel(model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<DateRange> model = getModel();

        IModel<LocalDateTime> startDateTimeModel = LambdaModel.of(model, DateRange::getStartDateTime,
            DateRange::setStartDateTime);
        DateTimeLocalTextField startDateTime = new DateTimeLocalTextField("startDateTime", startDateTimeModel);
        add(startDateTime);

        AjaxSubmitLink startDateTimeReset = new AjaxSubmitLink("startDateTimeReset", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                startDateTime.setModelObject(null);
                target.add(form);
            }
        };
        add(startDateTimeReset);


        IModel<LocalDateTime> endDateTimeModel = LambdaModel.of(model, DateRange::getEndDateTime,
            DateRange::setEndDateTime);
        DateTimeLocalTextField endDateTime = new DateTimeLocalTextField("endDateTime", endDateTimeModel);
        add(endDateTime);

        AjaxSubmitLink endDateTimeReset = new AjaxSubmitLink("endDateTimeReset", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                endDateTime.setModelObject(null);
                target.add(form);
            }
        };
        add(endDateTimeReset);
    }
}
