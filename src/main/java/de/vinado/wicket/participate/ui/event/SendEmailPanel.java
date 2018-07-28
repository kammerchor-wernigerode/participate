package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.Select2PersonProvider;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.service.EmailService;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;


/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SendEmailPanel extends BootstrapModalPanel<MailData> {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    @SuppressWarnings("unused")
    @SpringBean
    private EmailService emailService;

    public SendEmailPanel(final BootstrapModal modal, final IModel<MailData> model) {
        super(modal, new ResourceModel("email.new", "New Email"), model);
        setModalSize(ModalSize.Large);
        model.getObject().setFrom(ParticipateApplication.get().getApplicationProperties().getMail().getSender(), ParticipateApplication.get().getApplicationName());

        final TextField<String> fromTf = new TextField<>("from");
        fromTf.setEnabled(false);
        inner.add(fromTf);

        final Select2MultiChoice<Person> toTf = new Select2MultiChoice<>("to", new Select2PersonProvider(personService));
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
        emailService.send(model.getObject());
        Snackbar.show(target, new ResourceModel("email.send.success", "Email sent"));
    }

    @Override
    protected IModel<String> getSubmitBtnLabel() {
        return new ResourceModel("send", "Send");
    }
}
