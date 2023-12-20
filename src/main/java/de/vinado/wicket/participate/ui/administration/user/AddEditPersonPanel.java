package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.services.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class AddEditPersonPanel extends GenericPanel<PersonDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private final Form<PersonDTO> form;
    private boolean edit;

    public AddEditPersonPanel(String id, IModel<PersonDTO> model) {
        super(id, model);

        this.form = form("form");
        edit = null != model.getObject().getPerson();
    }

    protected Form<PersonDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                AddEditPersonPanel.this.onSubmit();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        RequiredTextField firstNameTf = new RequiredTextField("firstName");
        firstNameTf.setLabel(new ResourceModel("firstName", "Given Name"));
        form.add(firstNameTf, new FormComponentLabel("firstNameLabel", firstNameTf));

        RequiredTextField lastNameTf = new RequiredTextField("lastName");
        lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
        form.add(lastNameTf, new FormComponentLabel("lastNameLabel", lastNameTf));

        EmailTextField emailTf = new EmailTextField("email");
        emailTf.setLabel(new ResourceModel("email", "Email"));
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        form.add(emailTf, new FormComponentLabel("emailLabel", emailTf));
    }

    protected void onSubmit() {
        if (edit) {
            personService.savePerson(getModelObject());
        } else {
            personService.createPerson(getModelObject());
        }
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasPerson(email);
    }
}
