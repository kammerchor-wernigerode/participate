package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
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
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2Choice;

import static de.vinado.util.SerializablePredicates.not;

public abstract class AddPersonToUserPanel extends FormModal<AddUserDTO> {

    public static final String SELECTED_ASSIGN_PERSON = "person.assign";
    public static final String SELECTED_CREATE_PERSON = "person.add";

    private final IModel<String> selectedModel = Model.of();

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private Select2Choice<Person> personS2c;

    public AddPersonToUserPanel(ModalAnchor anchor, IModel<AddUserDTO> model) {
        super(anchor, model);

        title(new ResourceModel("person.assign", "Assign Person"));

        selectedModel.setObject(SELECTED_ASSIGN_PERSON);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupPlaceholderTag(true);
        wmc.setOutputMarkupId(true);
        wmc.setVisible(false);
        form.add(wmc);

        RadioGroup<String> radioGroup = new RadioGroup<>("personRC", selectedModel);
        radioGroup.setLabel(Model.of(""));
        radioGroup.add(new Radio<>("assignPersonChoice", Model.of(SELECTED_ASSIGN_PERSON)));
        radioGroup.add(new Radio<>("createPersonChoice", Model.of(SELECTED_CREATE_PERSON)));
        radioGroup.setRenderBodyOnly(false);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (SELECTED_CREATE_PERSON.equals(getSelectedModel().getObject())) {
                    wmc.setVisible(true);
                    personS2c.setVisible(false);
                    target.add(personS2c);
                    target.add(wmc);
                } else {
                    wmc.setVisible(false);
                    target.add(wmc);
                    personS2c.setVisible(true);
                    target.add(personS2c);
                }
            }
        });
        form.add(radioGroup);

        TextField firstNameTf = new TextField("firstName");
        firstNameTf.add(BootstrapHorizontalFormDecorator.decorate());
        firstNameTf.setRequired(true);
        wmc.add(firstNameTf);

        // surName
        TextField lastNameTf = new TextField("lastName");
        lastNameTf.add(BootstrapHorizontalFormDecorator.decorate());
        lastNameTf.setRequired(true);
        wmc.add(lastNameTf);

        // email
        EmailTextField emailTf = new EmailTextField("email");
        emailTf.add(BootstrapHorizontalFormDecorator.decorate());
        emailTf.setRequired(true);
        emailTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.email", "A person with this e-mail address already exists")));
        wmc.add(emailTf);

        // person
        personS2c = new Select2Choice<>("person",
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
        personS2c.add(BootstrapHorizontalFormDecorator.decorate());
        personS2c.add(new ConditionalValidator<>(not(userService::hasUser),
            new ResourceModel("person.assign.error", "The person is already assigned to a user")));
        form.add(personS2c);

        addBootstrapHorizontalFormDecorator(form);
    }

    private boolean ensureUnique(String email) {
        return StringUtils.equalsIgnoreCase(email, getModelObject().getEmail()) || !personService.hasPerson(email);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        onConfirm(userService.assignPerson(getModelObject()), target);
    }

    public IModel<String> getSelectedModel() {
        return selectedModel;
    }

    protected abstract void onConfirm(User user, AjaxRequestTarget target);
}
