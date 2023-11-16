package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class SendFeedbackPanel extends FormModal<SendFeedbackDTO> {

    @SpringBean
    private EmailService emailService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    public SendFeedbackPanel(ModalAnchor modal, IModel<SendFeedbackDTO> model) {
        super(modal, model);

        title(new ResourceModel("send.feedback", "Send Feedback"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        form.add(new TextField<>("name"));
        form.add(new EmailTextField("email"));
        form.add(new RequiredTextField<>("subject"));
        form.add(new TextArea<>("message").setRequired(true));

        addBootstrapHorizontalFormDecorator(form);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        SendFeedbackDTO dto = getModelObject();
        Email mailData = Email.builder("no-reply@vinado.de", dto.getName())
            .to("vincent.nadoll@gmail.com")
            .subject(dto.getSubject())
            .message(dto.getMessage())
            .build();

        emailService.send(mailData);
        Snackbar.show(target, new ResourceModel("send.feedback.success", "Thanks for your feedback"));
    }
}
