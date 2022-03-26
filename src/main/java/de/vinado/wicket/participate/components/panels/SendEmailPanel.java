package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.Size;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.providers.Select2EmailAddressProvider;
import de.vinado.wicket.participate.services.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

import javax.mail.internet.InternetAddress;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class SendEmailPanel extends FormModal<Email> {

    @SpringBean
    private PersonService personService;

    @SpringBean
    private EmailService emailService;

    public SendEmailPanel(ModalAnchor anchor, IModel<Email> model) {
        super(anchor, model);

        size(Size.Extra_large);
        title(new ResourceModel("email.new", "New Email"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final TextField<String> fromTf = new TextField<>("from");
        fromTf.setEnabled(false);
        form.add(fromTf);

        final Select2MultiChoice<InternetAddress> toTf = new Select2MultiChoice<>("to", new Select2EmailAddressProvider(personService));
        toTf.getSettings().setLanguage(getLocale().getLanguage());
        toTf.getSettings().setCloseOnSelect(true);
        toTf.getSettings().setTheme(new Select2BootstrapTheme(true));
        toTf.getSettings().setPlaceholder(new ResourceModel("select.placeholder", "Please Choose").getObject());
        toTf.getSettings().setDropdownParent(form.getMarkupId());
        toTf.setLabel(new ResourceModel("email.recipient", "Recipient"));
        form.add(toTf);

        final TextField<String> subjectTf = new TextField<>("subject");
        form.add(subjectTf);

        final TextArea<String> messageTa = new TextArea<>("message");
        form.add(messageTa);

        addBootstrapFormDecorator(form);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        Email mail = getModelObject();
        Stream<Email> mails = mail.toSingleRecipient();
        emailService.send(mails);

        Snackbar.show(target, new ResourceModel("email.send.success", "Email sent"));
    }

    @Override
    protected IModel<String> submitButtonLabel() {
        return new ResourceModel("send", "Send");
    }
}
