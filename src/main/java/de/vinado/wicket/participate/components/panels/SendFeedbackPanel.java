package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import de.vinado.wicket.participate.model.email.MailData;
import de.vinado.wicket.participate.services.EmailService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.mail.internet.AddressException;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SendFeedbackPanel extends BootstrapModalPanel<SendFeedbackDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private EmailService emailService;

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
        final MailData mailData = new MailData();

        try {
            mailData.setFrom("no-reply@vinado.de", model.getObject().getName());
            mailData.addTo("vincent@vinado.de");
            mailData.setSubject(model.getObject().getSubject());
            mailData.setMessage(model.getObject().getMessage());

            emailService.send(mailData);
            Snackbar.show(target, new ResourceModel("send.feedback.success", "Thanks for your feedback"));
        } catch (AddressException e) {
            e.printStackTrace();
        }
    }
}
