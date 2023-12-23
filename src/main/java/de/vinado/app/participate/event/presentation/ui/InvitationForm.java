package de.vinado.app.participate.event.presentation.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.Icon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.app.participate.wicket.bt5.form.DateTimeTextField;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipBehavior;
import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.common.DateUtils;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Accommodation.Status;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.wicket.form.ui.AccommodationFormGroup;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvitationForm extends GenericPanel<ParticipantDTO> {

    @SpringBean
    private EventService eventService;

    @SpringBean
    private ApplicationProperties applicationProperties;

    private FormComponent<InvitationStatus> invitationStatusSelect;
    private FormComponent<Date> fromTextField;
    private FormComponent<Date> toTextField;
    private FormComponent<Accommodation> accommodationFormGroup;
    private FormComponent<Boolean> carCheckbox;
    private FormComponent<Short> carSeatCountTextField;
    private FormComponent<String> commentTextField;

    private final Form<ParticipantDTO> form;

    public InvitationForm(String id, IModel<ParticipantDTO> model) {
        super(id, model);
        this.form = form("form");
    }

    protected Form<ParticipantDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                save(getModelObject());
            }
        };
    }

    private void save(ParticipantDTO dto) {
        eventService.saveParticipant(dto);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        form.setOutputMarkupId(true);

        queue(form);
        queue(toTextField = toTextField("to"));
        queue(toTextFieldLabel("toLabel"));
        queue(fromTextField = fromTextField("from"));
        queue(fromTextFieldLabel("fromLabel"));
        queue(accommodationFormGroup = accommodationFormGroup("accommodation"));
        queue(accommodationFormGroupLabel("accommodationLabel"));
        queue(carSeatCountTextField = carSeatCountTextField("carSeatCount"));
        queue(carSeatCountTextFieldLabel("carSeatCountLabel"));
        queue(carCheckbox = carCheckbox("car"));
        queue(carCheckboxLabel("carLabel"));
        queue(commentTextField = commentTextField("comment"));
        queue(commentTextFieldLabel("commentLabel"));
        queue(invitationStatusSelect = invitationStatusSelect("invitationStatus"));
        queue(invitationStatusSelectLabel("invitationStatusLabel"));
        queue(invitationLink("invitationLink"));
    }

    protected FormComponent<InvitationStatus> invitationStatusSelect(String wicketId) {
        fromTextField.setOutputMarkupId(true);
        toTextField.setOutputMarkupId(true);
        accommodationFormGroup.setOutputMarkupId(true);
        carCheckbox.setOutputMarkupId(true);
        carSeatCountTextField.setOutputMarkupId(true);
        commentTextField.setOutputMarkupId(true);

        IModel<InvitationStatus> model = LambdaModel.of(getModel(), ParticipantDTO::getInvitationStatus, ParticipantDTO::setInvitationStatus);
        List<InvitationStatus> choices = InvitationStatus.stream().collect(Collectors.toList());
        EnumChoiceRenderer<InvitationStatus> renderer = new EnumChoiceRenderer<>();
        FormComponent<InvitationStatus> select = new BootstrapSelect<>(wicketId, model, choices, renderer);
        select.setLabel(new ResourceModel("status", "Status"));
        select.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                FormComponent<InvitationStatus> component = (FormComponent<InvitationStatus>) getFormComponent();
                InvitationStatus selected = component.getConvertedInput();

                if (isNoteWorthy(selected)) {
                    return;
                }

                reset(target
                    , fromTextField
                    , toTextField
                    , accommodationFormGroup
                    , carCheckbox
                    , carSeatCountTextField
                    , commentTextField
                );
            }

            private boolean isNoteWorthy(InvitationStatus selected) {
                return List.of(InvitationStatus.ACCEPTED, InvitationStatus.TENTATIVE).contains(selected);
            }

            private void reset(AjaxRequestTarget target, FormComponent<?>... formComponents) {
                Arrays.stream(formComponents)
                    .forEach(formComponent -> reset(formComponent, target));
            }

            private void reset(FormComponent<?> formComponent, AjaxRequestTarget target) {
                formComponent.setDefaultModelObject(defaultValue(formComponent));
                target.add(formComponent);
            }

            private Object defaultValue(FormComponent<?> formComponent) {
                Class<?> type = formComponent.getType();
                if (type.isAssignableFrom(Boolean.class)) {
                    return false;
                } else if (type.isAssignableFrom(Short.class)) {
                    return (short) 0;
                } else if (type.isAssignableFrom(Accommodation.class)) {
                    return new Accommodation(Status.NO_NEED, null);
                } else {
                    return null;
                }
            }
        });
        return select;
    }

    protected Component invitationStatusSelectLabel(String wicketId) {
        return new FormComponentLabel(wicketId, invitationStatusSelect);
    }

    protected FormComponent<Date> toTextField(String wicketId) {
        IModel<Date> model = LambdaModel.of(getModel(), ParticipantDTO::getToDate, ParticipantDTO::setToDate);
        TempusDominusConfig config = createTempusDominusConfig(getModelObject().getEvent());
        FormComponent<Date> textField = new DateTimeTextField(wicketId, model, config);
        textField.setLabel(new ResourceModel("till", "Till"));
        return textField;
    }

    protected Component toTextFieldLabel(String wicketId) {
        return new FormComponentLabel(wicketId, toTextField);
    }

    protected FormComponent<Date> fromTextField(String wicketId) {
        IModel<Date> model = LambdaModel.of(getModel(), ParticipantDTO::getFromDate, ParticipantDTO::setFromDate);
        TempusDominusConfig config = createTempusDominusConfig(getModelObject().getEvent());
        FormComponent<Date> textField = new DateTimeTextField(wicketId, model, config) {

            @Override
            protected Stream<HeaderItem> additionalHeaderItems(Component component) {
                String source = component.getMarkupId();
                String target = toTextField.getMarkupId();
                return Stream.of(linkMinDate(source, target));
            }
        };
        textField.setLabel(new ResourceModel("from", "From"));
        return textField;
    }

    protected Component fromTextFieldLabel(String wicketId) {
        return new FormComponentLabel(wicketId, fromTextField);
    }

    protected FormComponent<Accommodation> accommodationFormGroup(String wicketId) {
        IModel<Accommodation> model = LambdaModel.of(getModel(), ParticipantDTO::getAccommodation, ParticipantDTO::setAccommodation);
        FormComponent<Accommodation> formGroup = new AccommodationFormGroup(wicketId, model);
        formGroup.setType(Accommodation.class);
        formGroup.setLabel(new ResourceModel("event.participant.dialog.form.label.accommodation", "Accommodation"));
        return formGroup;
    }

    protected Component accommodationFormGroupLabel(String wicketId) {
        IModel<String> model = accommodationFormGroup.getLabel();
        return new Label(wicketId, model);
    }

    protected FormComponent<Short> carSeatCountTextField(String wicketId) {
        IModel<Short> model = LambdaModel.of(getModel(), ParticipantDTO::getCarSeatCount, ParticipantDTO::setCarSeatCount);
        FormComponent<Short> carSeatCountTextField = new NumberTextField<>(wicketId, model, Short.class) {

            @Override
            protected void onInitialize() {
                super.onInitialize();
                setMinimum((short) 0);
                setMaximum((short) 127); // 1 Byte maximum signed integer
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (!InvitationForm.this.getModelObject().isCar()) {
                    InvitationForm.this.getModelObject().setCarSeatCount((short) 0);
                }
                setEnabled(InvitationForm.this.getModelObject().isCar());
            }
        };
        carSeatCountTextField.setLabel(new ResourceModel("carSeatCount", "Number of Seats"));
        return carSeatCountTextField;
    }

    protected Component carSeatCountTextFieldLabel(String wicketId) {
        return new FormComponentLabel(wicketId, carSeatCountTextField);
    }

    protected FormComponent<Boolean> carCheckbox(String wicketId) {
        carSeatCountTextField.setOutputMarkupId(true);

        IModel<Boolean> model = LambdaModel.of(getModel(), ParticipantDTO::isCar, ParticipantDTO::setCar);
        AjaxCheckBox checkbox = new AjaxCheckBox(wicketId, model) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(carSeatCountTextField);
            }
        };
        checkbox.setLabel(new ResourceModel("event.participant.dialog.form.label.car", "Uses own car"));
        return checkbox;
    }

    protected Component carCheckboxLabel(String wicketId) {
        carCheckbox.add(AttributeAppender.replace("aria-label", carCheckbox.getLabel()));

        Icon icon = new Icon(wicketId, FontAwesome5IconType.car_s);
        icon.add(new TooltipBehavior(carCheckbox.getLabel()));
        return icon;
    }

    protected FormComponent<String> commentTextField(String wicketId) {
        IModel<String> model = LambdaModel.of(getModel(), ParticipantDTO::getComment, ParticipantDTO::setComment);
        TextArea<String> textArea = new TextArea<>(wicketId, model);
        textArea.setType(String.class);
        textArea.setLabel(new ResourceModel("comment", "Comment"));
        textArea.add(new AutosizeBehavior());
        return textArea;
    }

    protected Component commentTextFieldLabel(String wicketId) {
        return new FormComponentLabel(wicketId, commentTextField);
    }

    protected AbstractLink invitationLink(String wicketId) {
        IModel<String> model = getModel().map(this::invitationUrl).map(URL::toString);
        return new ExternalLink(wicketId, model, model);
    }

    private URL invitationUrl(ParticipantDTO dto) {
        String baseUrl = applicationProperties.getBaseUrl();
        String token = dto.getToken();
        return ParticipateUtils.generateInvitationLink(baseUrl, token);
    }

    private static TempusDominusConfig createTempusDominusConfig(Event event) {
        return new TempusDominusConfig()
            .withUseCurrent(false)
            .withStepping(30)
            .withViewDate(new Date(event.getStartDate().getTime()))
            .withDisplay(display -> display
                .withSideBySide(true))
            .withRestrictions(restrictions -> restrictions
                .withMinDate(DateUtils.atStartOfDay(event.getStartDate()))
                .withMaxDate(DateUtils.atEndOfDay(event.getEndDate())));
    }
}
