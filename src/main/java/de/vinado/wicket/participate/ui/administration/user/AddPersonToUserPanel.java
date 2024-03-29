package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.providers.Select2PersonProvider;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2Choice;

import static de.vinado.util.SerializablePredicates.not;

public class AddPersonToUserPanel extends GenericPanel<AddUserDTO> {

    public static final String SELECTED_ASSIGN_PERSON = "person.assign";
    public static final String SELECTED_CREATE_PERSON = "person.add";

    private final IModel<String> selectedModel = Model.of();

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public AddPersonToUserPanel(String wicketId, IModel<AddUserDTO> model) {
        super(wicketId, model);

        selectedModel.setObject(SELECTED_ASSIGN_PERSON);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Form<AddUserDTO> form;
        add(form = form("form"));

        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupPlaceholderTag(true);
        wmc.setOutputMarkupId(true);
        wmc.setVisible(false);
        form.add(wmc);

        WebMarkupContainer personWmc = new WebMarkupContainer("personWmc");
        personWmc.setOutputMarkupPlaceholderTag(true);
        form.add(personWmc);

        RadioGroup<String> radioGroup = new RadioGroup<>("personRC", selectedModel);

        Radio<String> assignPersonChoice;
        radioGroup.add(assignPersonChoice = new Radio<>("assignPersonChoice", Model.of(SELECTED_ASSIGN_PERSON)));
        assignPersonChoice.setLabel(new ResourceModel("person.assign", "Assign Person"));
        radioGroup.add(new SimpleFormComponentLabel("assignPersonChoiceLabel", assignPersonChoice));

        Radio<String> createPersonChoice;
        radioGroup.add(createPersonChoice = new Radio<>("createPersonChoice", Model.of(SELECTED_CREATE_PERSON)));
        createPersonChoice.setLabel(new ResourceModel("person.add", "Add Person"));
        radioGroup.add(new SimpleFormComponentLabel("createPersonChoiceLabel", createPersonChoice));

        radioGroup.setRenderBodyOnly(false);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (SELECTED_CREATE_PERSON.equals(getSelectedModel().getObject())) {
                    wmc.setVisible(true);
                    personWmc.setVisible(false);
                    target.add(personWmc);
                    target.add(wmc);
                } else {
                    wmc.setVisible(false);
                    target.add(wmc);
                    personWmc.setVisible(true);
                    target.add(personWmc);
                }
            }
        });
        form.add(radioGroup);

        TextField firstNameTf = new TextField("firstName");
        firstNameTf.setLabel(new ResourceModel("firstName", "Given name"));
        firstNameTf.setRequired(true);
        wmc.add(firstNameTf, new FormComponentLabel("firstNameLabel", firstNameTf));

        // surName
        TextField lastNameTf = new TextField("lastName");
        lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
        lastNameTf.setRequired(true);
        wmc.add(lastNameTf, new FormComponentLabel("lastNameLabel", lastNameTf));

        // email
        EmailTextField emailTf = new EmailTextField("email");
        emailTf.setLabel(new ResourceModel("email", "Email"));
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        wmc.add(emailTf, new FormComponentLabel("emailLabel", emailTf));

        // person
        Select2Choice<Person> personS2c = new Select2Choice<>("person",
            new PropertyModel<>(getModel(), "person"),
            new Select2PersonProvider(personService));
        personS2c.getSettings().setLanguage(getLocale().getLanguage());
        personS2c.getSettings().setCloseOnSelect(true);
        personS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        personS2c.getSettings().setMinimumInputLength(3);
        personS2c.getSettings().setPlaceholder(new ResourceModel("select.placeholder", "Please Choose").getObject());
        personS2c.getSettings().setDropdownParent(form.getMarkupId());
        personS2c.setRequired(true);
        personS2c.setOutputMarkupPlaceholderTag(true);
        personS2c.setEnabled(true);
        personS2c.setLabel(new ResourceModel("person.select", "Select Person"));
        personS2c.setLabel(new ResourceModel("person", "Person"));
        personS2c.add(new ConditionalValidator<>(not(userService::hasUser),
            new ResourceModel("person.assign.error", "The person is already assigned to a user")));
        personWmc.add(personS2c, new FormComponentLabel("personLabel", personS2c));
    }

    protected Form<AddUserDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                AddPersonToUserPanel.this.onSubmit();
            }
        };
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasPerson(email);
    }

    protected void onSubmit() {
        User user = userService.assignPerson(getModelObject());
        Person person = user.getPerson();
        userService.startPasswordReset(person.getEmail(), true);
    }

    public IModel<String> getSelectedModel() {
        return selectedModel;
    }
}
