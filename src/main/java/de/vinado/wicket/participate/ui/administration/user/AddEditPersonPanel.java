package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
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
public abstract class AddEditPersonPanel extends BootstrapModalPanel<PersonDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private boolean edit;

    public AddEditPersonPanel(final BootstrapModal modal, final IModel<String> labelModel, final IModel<PersonDTO> model) {
        super(modal, labelModel, model);
        edit = null != model.getObject().getPerson();

        final RequiredTextField firstNameTf = new RequiredTextField("firstName");
        inner.add(firstNameTf);

        final RequiredTextField lastNameTf = new RequiredTextField("lastName");
        inner.add(lastNameTf);

        final EmailTextField emailTf = new EmailTextField("email");
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUniqueness,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        inner.add(emailTf);

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<PersonDTO> model, final AjaxRequestTarget target) {
        if (edit) {
            personService.savePerson(model.getObject());
        } else {
            personService.createPerson(model.getObject());
        }
        onUpdate(target);
    }

    private boolean ensureUniqueness(String email) {
        return !StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) && personService.hasPerson(email);
    }

    protected abstract void onUpdate(final AjaxRequestTarget target);
}
