package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapRadioChoice;
import de.vinado.wicket.participate.model.Accommodation;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.AbstractChoice;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.IntegerConverter;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.vinado.wicket.participate.model.Accommodation.Status;

public class AccommodationFormGroup extends FormComponentPanel<Accommodation> {

    private FormComponent<Status> status;
    private FormComponent<Integer> beds;

    public AccommodationFormGroup(String id, IModel<Accommodation> model) {
        super(id, model);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(status = status("status"));
        add(beds = beds("beds"));

        add(new Validator());
    }

    private FormComponent<Status> status(String wicketId) {
        IModel<Status> model = lambda(Accommodation::getStatus, Accommodation::setStatus);
        List<Status> choices = Arrays.asList(Status.values());
        EnumChoiceRenderer<Status> renderer = new EnumChoiceRenderer<>(this);
        BootstrapRadioChoice<Status> control = new BootstrapRadioChoice<>(wicketId, model, choices, renderer);
        control.setOutputMarkupId(true);
        control.setInline(true);
        control.setRequired(true);
        control.setLabel(new ResourceModel("event.participant.form.control.accommodation.status"));
        control.setLabelPosition(AbstractChoice.LabelPosition.AFTER);
        control.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(beds);
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);

                attributes.setEventPropagation(AjaxRequestAttributes.EventPropagation.STOP);
            }
        });
        return control;
    }

    private FormComponent<Integer> beds(String wicketId) {
        IModel<Integer> model = lambda(Accommodation::getBeds, Accommodation::setBeds);
        NumberTextField<Integer> control = new NumberTextField<>(wicketId, model) {

            @Override
            protected IConverter<?> createConverter(Class<?> type) {
                return IntegerConverter.INSTANCE;
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();

                setVisible(isQuantifiable());
            }
        };
        control.setOutputMarkupPlaceholderTag(true);
        control.setMinimum(1);
        control.setStep(1);
        control.setLabel(new ResourceModel("event.participant.form.control.accommodation.beds"));
        control.add(new CssClassNameAppender("form-control"));
        return control;
    }

    private <R> IModel<R> lambda(SerializableFunction<Accommodation, R> getter,
                                 SerializableBiConsumer<Accommodation, R> setter) {
        IModel<Accommodation> model = getModel();
        return LambdaModel.of(model, getter, setter);
    }

    private boolean isQuantifiable() {
        Status status = status();
        return status.isQuantifiable();
    }

    private Status status() {
        return Optional.ofNullable(this.status.getConvertedInput())
            .orElseGet(getModelObject()::getStatus);
    }

    @Override
    public void convertInput() {
        Status status = this.status.getConvertedInput();
        Integer beds = this.beds.getConvertedInput();

        Accommodation accommodation = getModelObject();
        accommodation.setStatus(status);
        accommodation.setBeds(beds);

        setConvertedInput(accommodation);
    }


    public static class Validator implements IValidator<Accommodation> {

        @Override
        public void validate(IValidatable<Accommodation> validatable) {
            Accommodation accommodation = validatable.getValue();
            if (accommodation.isQuantifiable()) {
                assertNotNull(validatable);
            }
        }

        private static void assertNotNull(IValidatable<Accommodation> validatable) {
            Accommodation accommodation = validatable.getValue();
            Integer beds = accommodation.getBeds();
            if (null == beds) {
                validatable.error((IValidationError) source -> {
                    Map<String, Object> vars = Collections.emptyMap();
                    String key = "event.participant.form.validation.accommodation.beds";
                    return source.getMessage(key, vars);
                });
            }
        }
    }
}
