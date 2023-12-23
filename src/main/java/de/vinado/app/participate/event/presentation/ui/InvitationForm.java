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
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.net.URL;
import java.util.Date;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class InvitationForm extends GenericPanel<ParticipantDTO> {

    @SpringBean
    private EventService eventService;

    @SpringBean
    private ApplicationProperties applicationProperties;

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
        queue(carSeatCountTextField = carSeatCountTextField("carSeatCount"));
        queue(carSeatCountTextFieldLabel("carSeatCountLabel"));
        queue(carCheckbox = carCheckbox("car"));
        queue(carCheckboxLabel("carLabel"));
        queue(commentTextField = commentTextField("comment"));
        queue(commentTextFieldLabel("commentLabel"));
        queue(invitationLink("invitationLink"));

        Event event = getModelObject().getEvent();
        TempusDominusConfig fromConfig = createTempusDominusConfig(event);
        TempusDominusConfig toConfig = createTempusDominusConfig(event);

        BootstrapSelect<InvitationStatus> invitationStatusBs = new BootstrapSelect<>("invitationStatus",
            InvitationStatus.stream().collect(toList()), new EnumChoiceRenderer<>());
        invitationStatusBs.add(new AjaxFormComponentUpdatingBehavior("hidden.bs.select") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                getModelObject().setInvitationStatus(invitationStatusBs.getConvertedInput());
                target.add(form);
            }
        });
        form.add(invitationStatusBs);

        IModel<Date> toModel = LambdaModel.of(getModel(), ParticipantDTO::getToDate, ParticipantDTO::setToDate);
        DateTimeTextField toDtP = new DateTimeTextField("toDate", toModel, toConfig);

        IModel<Date> fromModel = LambdaModel.of(getModel(), ParticipantDTO::getFromDate, ParticipantDTO::setFromDate);
        FormComponent<Date> fromDtP = new DateTimeTextField("fromDate", fromModel, fromConfig) {

            @Override
            protected Stream<HeaderItem> additionalHeaderItems(Component component) {
                String source = component.getMarkupId();
                String target = toDtP.getMarkupId();
                return Stream.of(linkMinDate(source, target));
            }
        };
        fromDtP.setLabel(new ResourceModel("from", "From"));
        fromDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(fromDtP, new FormComponentLabel("fromDateLabel", fromDtP));

        toDtP.setLabel(new ResourceModel("till", "Till"));
        toDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        form.add(toDtP, new FormComponentLabel("toDateLabel", toDtP));

        IModel<Accommodation> model = LambdaModel.of(getModel(), ParticipantDTO::getAccommodation, ParticipantDTO::setAccommodation);
        AccommodationFormGroup accommodationCb = new AccommodationFormGroup("accommodation", model);
        accommodationCb.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {

            }
        });
        form.add(accommodationCb);
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
