package de.vinado.wicket.participate.wicket.form.ui;

import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.bt4.modal.TextContentModal;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.TemplateModel;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.BasePage;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.HashMap;
import java.util.Map;

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

        add(form("form"));
    }

    protected Form<ParticipantDTO> form(String id) {
        return new ParticipantForm(id, getModel()) {

            @Override
            protected void onAcceptEvent(AjaxRequestTarget target) {
                eventService.acceptEvent(getModelObject());
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());

                displayConfirmation(getModelObject().getParticipant(), target);
            }

            @Override
            protected void onDeclineEvent(AjaxRequestTarget target) {
                eventService.declineEvent(getModelObject());
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());

                ResourceModel model = new ResourceModel("invitation.decline.success",
                    "Your cancellation has been saved. You can leave this page now.");
                Snackbar.show(target, model);
            }

            @Override
            protected void onAcceptTentatively(AjaxRequestTarget target) {
                eventService.acceptEventTentatively(getModelObject());
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());

                TemplateModel templateModel = new TemplateModel("invitation.tentative.success-txt.ftl");
                displaySuccessionModal(templateModel, target);
            }
        };
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
