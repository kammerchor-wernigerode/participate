package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
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
public class SendFeedbackPanel extends BootstrapModalPanel<SendFeedbackDTO> {

    @SpringBean
    private EmailService emailService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    /**
     * @param modal {@link de.vinado.wicket.participate.components.modals.BootstrapModal}
     * @param model Model
     */
    public SendFeedbackPanel(final BootstrapModal modal, final IModel<SendFeedbackDTO> model) {
        super(modal, new ResourceModel("send.feedback", "Send Feedback"), model);

        inner.add(new TextField<>("name"));
        inner.add(new EmailTextField("email"));
        inner.add(new RequiredTextField<>("subject"));
        inner.add(new TextArea<>("message").setRequired(true));

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<SendFeedbackDTO> model, final AjaxRequestTarget target) {
        SendFeedbackDTO dto = model.getObject();
        Email mailData = Email.builder("no-reply@vinado.de", dto.getName())
            .to("vincent.nadoll@gmail.com")
            .subject(dto.getSubject())
            .message(dto.getMessage())
            .build();

        emailService.send(mailData);
        Snackbar.show(target, new ResourceModel("send.feedback.success", "Thanks for your feedback"));
    }
}
