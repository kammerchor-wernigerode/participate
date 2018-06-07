package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.validator.ConditionalValidator;
import de.vinado.wicket.participate.data.dto.PersonDTO;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

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
        emailTf.add(new ConditionalValidator<String>(new ResourceModel("unique.email", "A person with this e-mail address already exists")) {
            @Override
            public boolean getCondition(final String value) {
                if (Strings.isEmpty(model.getObject().getEmail())) {
                    return false;
                } else if (value.equals(model.getObject().getEmail())) {
                    return false;
                }

                return personService.hasPerson(value);
            }
        });
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

    protected abstract void onUpdate(final AjaxRequestTarget target);
}
