package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.vinado.app.participate.wicket.bt5.form.DateTimeTextField;
import de.vinado.wicket.participate.common.DateUtils;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.ui.event.ParticipantFilterForm;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.head.HeaderItem;
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

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

public abstract class DetailedParticipantFilterForm extends ParticipantFilterForm {

    private Component toTextField;

    private final IModel<Event> eventModel;

    public DetailedParticipantFilterForm(String id, IModel<ParticipantFilter> model, IModel<Event> eventModel) {
        super(id, model);
        this.eventModel = eventModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(comment("comment"));
        queue(accommodation("accommodation"));

        queue(toDate("toDate"));
        queue(fromDate("fromDate"));
    }

    protected MarkupContainer comment(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<String> model = LambdaModel.of(getModel(), ParticipantFilter::getComment, ParticipantFilter::setComment);
        FormComponent<String> control = new TextField<>("control", model)
            .setLabel(new ResourceModel("filter.participant.form.control.comment", "Filter by comments"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer fromDate(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), ParticipantFilter::getFromDate, ParticipantFilter::setFromDate);
        TempusDominusConfig config = createTempusDominusConfig(eventModel.getObject());
        FormComponent<Date> control = new DateTimeTextField("control", model, config) {

            @Override
            protected Stream<HeaderItem> additionalHeaderItems(Component component) {
                String source = component.getMarkupId();
                String target = toTextField.getMarkupId();
                return Stream.of(linkMinDate(source, target));
            }
        };
        control.setLabel(new ResourceModel("filter.participant.form.control.from", "From"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(control, label);
    }

    protected MarkupContainer toDate(String wicketId) {
        WebMarkupContainer container = new WebMarkupContainer(wicketId);

        IModel<Date> model = LambdaModel.of(getModel(), ParticipantFilter::getToDate, ParticipantFilter::setToDate);
        TempusDominusConfig config = createTempusDominusConfig(eventModel.getObject());
        FormComponent<Date> control = new DateTimeTextField("control", model, config);
        control.setLabel(new ResourceModel("filter.participant.form.control.to", "To"));
        FormComponentLabel label = new SimpleFormComponentLabel("label", control);

        return container.add(toTextField = control, label);
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

    private static TempusDominusConfig createTempusDominusConfig(Event event) {
        return new TempusDominusConfig()
            .withUseCurrent(false)
            .withStepping(30)
            .withViewDate(new Date(event.getStartDate().getTime()))
            .withRestrictions(restrictions -> restrictions
                .withMinDate(DateUtils.atStartOfDay(event.getStartDate()))
                .withMaxDate(DateUtils.atEndOfDay(event.getEndDate())));
    }

    @Override
    protected void onDetach() {
        eventModel.detach();
        super.onDetach();
    }
}
