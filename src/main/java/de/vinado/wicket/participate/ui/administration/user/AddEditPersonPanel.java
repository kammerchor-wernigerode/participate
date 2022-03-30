package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddEditPersonPanel extends FormModal<PersonDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;

    public AddEditPersonPanel(ModalAnchor anchor, IModel<String> title, IModel<PersonDTO> model) {
        super(anchor, model);

        title(title);

        edit = null != model.getObject().getPerson();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final RequiredTextField firstNameTf = new RequiredTextField("firstName");
        form.add(firstNameTf);

        final RequiredTextField lastNameTf = new RequiredTextField("lastName");
        form.add(lastNameTf);

        final EmailTextField emailTf = new EmailTextField("email");
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        form.add(emailTf);

        addBootstrapHorizontalFormDecorator(form);
    }

    @Override
    protected void onSubmit(final AjaxRequestTarget target) {
        if (edit) {
            personService.savePerson(getModelObject());
        } else {
            personService.createPerson(getModelObject());
        }
        onUpdate(target);
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasPerson(email);
    }

    protected abstract void onUpdate(final AjaxRequestTarget target);
}
