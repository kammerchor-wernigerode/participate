package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.behavoirs.AutosizeBehavior;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.DismissableBootstrapModalPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.TemplateModel;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormPanel extends BreadCrumbPanel implements IGenericComponent<ParticipantDTO> {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SpringBean
    private ApplicationProperties applicationProperties;

    public FormPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<ParticipantDTO> model) {
        super(id, breadCrumbModel, model);

        final DatetimePickerConfig fromConfig = new DatetimePickerConfig();
        fromConfig.useLocale("de");
        fromConfig.useCurrent(false);
        fromConfig.withFormat("dd.MM.yyyy HH:mm");
        fromConfig.withMinuteStepping(30);

        final DatetimePickerConfig toConfig = new DatetimePickerConfig();
        toConfig.useLocale("de");
        toConfig.useCurrent(false);
        toConfig.withFormat("dd.MM.yyyy HH:mm");
        toConfig.withMinuteStepping(30);

        if (null != model.getObject().getEvent()) {
            fromConfig.withMinDate(model.getObject().getEvent().getStartDate());
            fromConfig.withMaxDate(model.getObject().getEvent().getEndDate());
            toConfig.withMinDate(model.getObject().getEvent().getStartDate());
            toConfig.withMaxDate(model.getObject().getEvent().getEndDate());
        }

        final Form<?> form = new Form<>("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        wmc.add(new Label("singer.displayName"));

        final DatetimePicker toDtP = new DatetimePicker("toDate", fromConfig);

        final DatetimePicker fromDtP = new DatetimePicker("fromDate", toConfig);
        fromDtP.add(new AjaxFormComponentUpdatingBehavior("dp.hide") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
                if (!Strings.isEmpty(fromDtP.getValue())) {
                    try {
                        toConfig.withMinDate(new SimpleDateFormat("dd.MM.yyyy").parse(fromDtP.getValue()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    target.add(toDtP);
                }
            }
        });
        fromDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("from", "From")));
        wmc.add(fromDtP);

        toDtP.setOutputMarkupId(true);
        toDtP.add(BootstrapHorizontalFormDecorator.decorate(new ResourceModel("till", "Till")));
        toDtP.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
            }
        });
        wmc.add(toDtP);

        final CheckBox cateringCb = new CheckBox("catering");
        cateringCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(cateringCb);

        final CheckBox accommodationCb = new CheckBox("accommodation");
        accommodationCb.add(BootstrapHorizontalFormDecorator.decorate());
        wmc.add(accommodationCb);

        NumberTextField<Integer> carSeatCountTf = new NumberTextField<Integer>("carSeatCount") {
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
        carSeatCountTf.setMinimum(0);
        carSeatCountTf.setMaximum(127); // 1 Byte maximum signed integer
        wmc.add(carSeatCountTf);

        AjaxCheckBox carCb = new AjaxCheckBox("car") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(carSeatCountTf);
            }
        };
        wmc.add(carCb);

        final TextArea<?> commentTa = new TextArea<>("comment");
        commentTa.setLabel(new ResourceModel("comments", "More comments"));
        commentTa.add(BootstrapHorizontalFormDecorator.decorate());
        commentTa.add(new AutosizeBehavior());
        wmc.add(commentTa);

        final BootstrapAjaxButton submitBtn = new BootstrapAjaxButton("submit", Buttons.Type.Success) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                ParticipantDTO dto = model.getObject();
                Participant updatedParticipant = eventService.acceptEvent(dto);
                EventUpdateEvent intent = new EventUpdateEvent(updatedParticipant.getEvent(), target);
                send(getPage(), Broadcast.BREADTH, intent);

                displayConfirmation(dto.getParticipant(), target);

                target.add(form);
            }
        };
        submitBtn.setLabel(new ResourceModel("save", "Save"));
        submitBtn.setSize(Buttons.Size.Small);
        wmc.add(submitBtn);

        final BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("decline", Buttons.Type.Default) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                final Participant savedParticipant = eventService.declineEvent(model.getObject());
                send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(savedParticipant.getEvent(), target));
                Snackbar.show(target, new ResourceModel("invitation.decline.success", "Your cancellation has been saved. You can leave this page now."));
                target.add(form);
            }
        };
        declineBtn.setLabel(new ResourceModel("decline", "Decline"));
        declineBtn.setSize(Buttons.Size.Small);
        wmc.add(declineBtn);
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
        BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        ResourceModel titleModel = new ResourceModel("invitation.accept.success", "Thanks you for your registration!");

        DismissableBootstrapModalPanel<String> confirmation = new DismissableBootstrapModalPanel<>(modal,
            titleModel, messageModel);
        modal.setContent(confirmation);
        modal.show(target);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("form", "Form");
    }

    @SuppressWarnings("unchecked")
    @Override
    public IModel<ParticipantDTO> getModel() {
        return (IModel<ParticipantDTO>) getDefaultModel();
    }

    @Override
    public void setModel(IModel<ParticipantDTO> model) {
        setDefaultModel(model);
    }

    @Override
    public void setModelObject(ParticipantDTO object) {
        setDefaultModelObject(object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ParticipantDTO getModelObject() {
        return (ParticipantDTO) getDefaultModelObject();
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
