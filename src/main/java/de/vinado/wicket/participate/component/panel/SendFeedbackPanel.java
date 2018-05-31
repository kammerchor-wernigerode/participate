package de.vinado.wicket.participate.component.panel;

import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.dto.SendFeedbackDTO;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.service.EmailService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SendFeedbackPanel extends BootstrapModalPanel<SendFeedbackDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private EmailService emailService;

    /**
     * @param modal {@link de.vinado.wicket.participate.component.modal.BootstrapModal}
     * @param model Model
     */
    public SendFeedbackPanel(final BootstrapModal modal, final IModel<SendFeedbackDTO> model) {
        super(modal, new ResourceModel("sendFeedback", "Send feedback"), model);

        inner.add(new TextField<>("name"));
        inner.add(new EmailTextField("email"));
        inner.add(new RequiredTextField<>("subject"));
        inner.add(new TextArea<>("message").setRequired(true));

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<SendFeedbackDTO> model, final AjaxRequestTarget target) {
        final MailData mailData = new MailData("no-reply@vinado.de", model.getObject().getName(),
                "vincent@vinado.de", model.getObject().getSubject()) {
            @Override
            public Map<String, Object> getData() {
                final Map<String, Object> map = super.getData();
                map.put("email", model.getObject().getEmail());
                map.put("message", model.getObject().getMessage());
                map.put("replyTo", true);
                return map;
            }
        };

        emailService.sendMail(mailData, "fm-feedback.ftl");

        Snackbar.show(target, new ResourceModel("sentMessage", "Message sent"));
    }
}
