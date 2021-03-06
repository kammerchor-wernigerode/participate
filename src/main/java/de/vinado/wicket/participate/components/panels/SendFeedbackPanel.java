package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.visit.IVisitor;

import javax.mail.internet.AddressException;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class SendFeedbackPanel extends Modal<SendFeedbackDTO> {

    @SpringBean
    private EmailService emailService;

    public SendFeedbackPanel(String id) {
        super(id, new CompoundPropertyModel<>(new SendFeedbackDTO()));

        BootstrapForm<SendFeedbackDTO> form = new BootstrapForm<>("form", getModel())
            .type(FormType.Default);

        NotificationPanel feedback = new NotificationPanel("feedback");
        feedback.setOutputMarkupId(true);

        BootstrapAjaxButton submitButton = new BootstrapAjaxButton("button", form, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                onSaveSubmit(target);
                close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(feedback);
            }
        }.setLabel(new ResourceModel("save"));

        TextField<Object> name = new TextField<>("name");

        EmailTextField email = new EmailTextField("email");

        RequiredTextField<Object> subject = new RequiredTextField<>("subject");

        TextArea<Object> message = new TextArea<>("message");
        message.setRequired(true);

        add(form.add(name, email, subject, message, feedback));

        form.visitChildren(FormComponent.class, (IVisitor<FormComponent<?>, Void>) (component, visit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapHorizontalFormDecorator.decorate());
            }
            visit.dontGoDeeper();
        });

        header(new ResourceModel("send.feedback", "Send Feedback"));
        addCloseButton(new ResourceModel("close"));
        addButton(submitButton);
        setFooterVisible(true);
    }

    protected void onSaveSubmit(final AjaxRequestTarget target) {
        SendFeedbackDTO dto = getModelObject();
        Email mailData = new Email();

        try {
            mailData.setFrom("no-reply@vinado.de", dto.getName());
            mailData.addTo("me@vinado.de");
            mailData.setSubject(dto.getSubject());
            mailData.setMessage(dto.getMessage());

            emailService.send(mailData);
            Snackbar.show(target, new ResourceModel("send.feedback.success", "Thanks for your feedback"));
        } catch (AddressException e) {
            log.error("Encountered malformed email address", e);
            Snackbar.show(target, Model.of("The application encountered a malformed email address. Abort."));
        }
    }
}
