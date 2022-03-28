package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.jquery.Key;
import de.vinado.wicket.bt4.button.BootstrapAjaxButton;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerIconConfig;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResetIntent;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerResettingBehavior;
import de.vinado.wicket.bt4.datetimepicker.DatetimePickerWidgetPositioningConfig;
import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.bt4.modal.TextContentModal;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.form.AutosizeBehavior;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.TemplateModel;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Nadoll
 */
public class FormPanel extends GenericPanel<ParticipantDTO> {

    @SpringBean
    private EventService eventService;

    @SpringBean
    private ApplicationProperties applicationProperties;

    public FormPanel(String id, IModel<ParticipantDTO> model) {
        super(id, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Event event = getModelObject().getEvent();
        DatetimePickerConfig fromConfig = createDatetimePickerConfig();
        DatetimePickerConfig toConfig = createDatetimePickerConfig();
        fromConfig.withMinDate(event.getStartDate());
        fromConfig.withMaxDate(DateUtils.addMilliseconds(DateUtils.addDays(event.getEndDate(), 1), -1));
        toConfig.withMinDate(event.getStartDate());
        toConfig.withMaxDate(DateUtils.addMilliseconds(DateUtils.addDays(event.getEndDate(), 1), -1));

        Form<?> form = new Form<>("form");
        add(form);

        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        wmc.add(new Label("singer.displayName"));

        DatetimePicker toDtP = new DatetimePicker("toDate", toConfig);

        WebMarkupContainer periodHelp;
        wmc.add(periodHelp = new WebMarkupContainer("periodHelp"));
        periodHelp.setOutputMarkupId(true);

        DatetimePicker fromDtP = new DatetimePicker("fromDate", fromConfig);
        fromDtP.add(new DatetimePickerResettingBehavior(toConfig::withMinDate));
        fromDtP.add(AttributeAppender.append("aria-describedby", periodHelp.getMarkupId()));
        wmc.add(fromDtP, new FormComponentLabel("fromDateLabel", fromDtP));

        toDtP.setOutputMarkupId(true);
        toDtP.add(new UpdateOnEventBehavior<>(DatetimePickerResetIntent.class));
        wmc.add(toDtP, new FormComponentLabel("toDateLabel", toDtP));

        CheckBox cateringCb = new CheckBox("catering");
        cateringCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(cateringCb);

        CheckBox accommodationCb = new CheckBox("accommodation");
        accommodationCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(accommodationCb);

        NumberTextField<Short> carSeatCountTf = new NumberTextField<>("carSeatCount") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                if (!FormPanel.this.getModelObject().isCar()) {
                    FormPanel.this.getModelObject().setCarSeatCount((short) 0);
                }
                setEnabled(FormPanel.this.getModelObject().isCar());
            }
        };
        carSeatCountTf.setOutputMarkupId(true);
        carSeatCountTf.setMinimum((short) 0);
        carSeatCountTf.setMaximum((short) 127); // 1 Byte maximum signed integer
        wmc.add(carSeatCountTf, new FormComponentLabel("carSeatCountLabel", carSeatCountTf));

        AjaxCheckBox carCb = new AjaxCheckBox("car") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(carSeatCountTf);
            }
        };
        wmc.add(carCb, new FormComponentLabel("carLabel", carCb));

        TextArea<?> commentTa = new TextArea<>("comment");
        commentTa.setLabel(new ResourceModel("comments", "More comments"));
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AutosizeBehavior());
        wmc.add(commentTa);

        BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit", Buttons.Type.Success) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                ParticipantDTO dto = FormPanel.this.getModelObject();
                Participant updatedParticipant = eventService.acceptEvent(dto);
                EventUpdateEvent intent = new EventUpdateEvent(updatedParticipant.getEvent(), target);
                send(getPage(), Broadcast.BREADTH, intent);

                displayConfirmation(dto.getParticipant(), target);

                target.add(form);
            }
        };
        submitBtn.setLabel(new ResourceModel("save", "Save"));
        wmc.add(submitBtn);

        BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("decline", Buttons.Type.Default) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                Participant savedParticipant = eventService.declineEvent(FormPanel.this.getModelObject());
                send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(savedParticipant.getEvent(), target));
                Snackbar.show(target, new ResourceModel("invitation.decline.success", "Your cancellation has been saved. You can leave this page now."));
                target.add(form);
            }
        };
        declineBtn.setLabel(new ResourceModel("decline", "Decline"));
        wmc.add(declineBtn);
    }

    private DatetimePickerConfig createDatetimePickerConfig() {
        Key<DatetimePickerWidgetPositioningConfig> positioningConfigKey = new Key<>("widgetPositioning");
        DatetimePickerWidgetPositioningConfig widgetPositioning = new DatetimePickerWidgetPositioningConfig()
            .withVerticalPositioning("bottom");
        DatetimePickerConfig config = new DatetimePickerConfig();
        config.useLocale("de");
        config.useCurrent(false);
        config.withFormat("dd.MM.yyyy HH:mm");
        config.withMinuteStepping(30);
        config.put(positioningConfigKey, widgetPositioning);
        config.put(new Key<>("date", null), "null");
        config.with(new DatetimePickerIconConfig());
        return config;
    }

    private void displayConfirmation(Participant participant, AjaxRequestTarget target) {
        boolean hasOrganizationResponsible = !Strings.isEmpty(applicationProperties.getOrganizationResponsible());
        boolean hasSleepingPlaceResponsible = !Strings.isEmpty(applicationProperties.getSleepingPlaceResponsible());
        Map<String, Object> templateData = mapSuccessionTemplateData(applicationProperties);

        if (eventService.hasDeadlineExpired(participant)) {
            if (hasOrganizationResponsible && hasSleepingPlaceResponsible) {
                prepareAndDisplaySuccessionModal(SuccessionMode.AFTER, templateData, target);
            }
        } else {
            if (hasOrganizationResponsible) {
                prepareAndDisplaySuccessionModal(SuccessionMode.BEFORE, templateData, target);
            }
        }
    }

    private void prepareAndDisplaySuccessionModal(SuccessionMode mode, Map<String, Object> templateData,
                                                  AjaxRequestTarget target) {
        IModel<String> messageModel = mode.getTemplateModel(templateData);
        displaySuccessionModal(messageModel, target);
    }

    private static Map<String, Object> mapSuccessionTemplateData(ApplicationProperties properties) {
        Map<String, Object> data = new HashMap<>();
        data.put("sleepingPlaceResponsible", properties.getSleepingPlaceResponsible());
        data.put("organizer", properties.getOrganizationResponsible());
        return data;
    }

    private void displaySuccessionModal(IModel<String> messageModel, AjaxRequestTarget target) {
        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        ResourceModel titleModel = new ResourceModel("invitation.accept.success", "Thanks you for your registration!");

        TextContentModal confirmation = new TextContentModal(modal, messageModel);
        confirmation.title(titleModel);
        modal.setContent(confirmation);
        modal.show(target);
    }

    @RequiredArgsConstructor
    private enum SuccessionMode {
        AFTER("registrationSuccess.afterDeadline-txt.ftl", ""
            + "Please contact the responsible person, as the deadline has already expired. So you can be sure that we have you on the screen.\n\n"
            + "Please note that you have to organize a sleeping place for yourself.\n\n"
            + "If you have general questions about the event, feel free to contact the person in charge."),
        BEFORE("registrationSuccess.beforeDeadline-txt.ftl", "If you have questions about the event, feel free to contact the responsible person."),
        ;

        private final String templateName;
        private final String defaultMessage;

        private TemplateModel getTemplateModel(Map<String, Object> data) {
            return new TemplateModel(templateName, defaultMessage, data);
        }
    }
}
