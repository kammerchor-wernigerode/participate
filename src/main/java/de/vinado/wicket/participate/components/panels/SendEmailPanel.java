package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.email.MailData;
import de.vinado.wicket.participate.providers.Select2EmailAddressProvider;
import de.vinado.wicket.participate.services.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;


/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class SendEmailPanel extends BootstrapModalPanel<MailData> {

    @SpringBean
    private PersonService personService;

    @SpringBean
    private EmailService emailService;

    public SendEmailPanel(final BootstrapModal modal, final IModel<MailData> model) {
        super(modal, new ResourceModel("email.new", "New Email"), model);
        setModalSize(ModalSize.Large);
        model.getObject().setFrom(ParticipateApplication.get().getApplicationProperties().getMail().getSender(), ParticipateApplication.get().getApplicationName());

        final TextField<String> fromTf = new TextField<>("from");
        fromTf.setEnabled(false);
        inner.add(fromTf);

        final Select2MultiChoice<InternetAddress> toTf = new Select2MultiChoice<>("to", new Select2EmailAddressProvider(personService));
        toTf.getSettings().setLanguage(getLocale().getLanguage());
        toTf.getSettings().setCloseOnSelect(true);
        toTf.getSettings().setTheme(new Select2BootstrapTheme(true));
        toTf.getSettings().setPlaceholder(new ResourceModel("select.placeholder", "Please Choose").getObject());
        toTf.setLabel(new ResourceModel("email.recipient", "Recipient"));
        inner.add(toTf);

        final TextField<String> subjectTf = new TextField<>("subject");
        inner.add(subjectTf);

        final TextArea<String> messageTa = new TextArea<>("message");
        inner.add(messageTa);

        addBootstrapFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<MailData> model, final AjaxRequestTarget target) {
        final MailData mailData = model.getObject();
        final Email email = new Email();
        try {
            email.setFrom(mailData.getFrom().getAddress(), mailData.getFrom().getPersonal());

            email.setTo(new HashSet<>(mailData.getTo()));
            email.setSubject(mailData.getSubject());
            email.setMessage(mailData.getMessage());

            emailService.send(email);
            Snackbar.show(target, new ResourceModel("email.send.success", "Email sent"));
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding is not supported", e);
            Snackbar.show(target, Model.of("The application encountered an unsupported encoding"));
        }
    }

    @Override
    protected IModel<String> getSubmitBtnLabel() {
        return new ResourceModel("send", "Send");
    }
}
