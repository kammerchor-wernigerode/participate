package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.forms.validator.ConditionalValidator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.dto.AddUserDTO;
import de.vinado.wicket.participate.providers.Select2PersonProvider;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
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
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2Choice;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddPersonToUserPanel extends BootstrapModalPanel<AddUserDTO> {

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

    /**
     * @param modal {@link de.vinado.wicket.participate.components.modals.BootstrapModal}
     * @param model Model
     */
    public AddPersonToUserPanel(final BootstrapModal modal, final IModel<AddUserDTO> model) {
        super(modal, new ResourceModel("person.assign", "Assign Person"), model);

        selectedModel.setObject(SELECTED_ASSIGN_PERSON);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupPlaceholderTag(true);
        wmc.setOutputMarkupId(true);
        wmc.setVisible(false);
        inner.add(wmc);

        final RadioGroup<String> radioGroup = new RadioGroup<>("personRC", selectedModel);
        radioGroup.setLabel(Model.of(""));
        radioGroup.add(new Radio<>("assignPersonChoice", Model.of(SELECTED_ASSIGN_PERSON)));
        radioGroup.add(new Radio<>("createPersonChoice", Model.of(SELECTED_CREATE_PERSON)));
        radioGroup.setRenderBodyOnly(false);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(final AjaxRequestTarget target) {
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
        inner.add(radioGroup);

        final TextField firstNameTf = new TextField("firstName");
        firstNameTf.add(BootstrapHorizontalFormDecorator.decorate());
        firstNameTf.setRequired(true);
        wmc.add(firstNameTf);

        // surName
        final TextField lastNameTf = new TextField("lastName");
        lastNameTf.add(BootstrapHorizontalFormDecorator.decorate());
        lastNameTf.setRequired(true);
        wmc.add(lastNameTf);

        // email
        final EmailTextField emailTf = new EmailTextField("email");
        emailTf.add(BootstrapHorizontalFormDecorator.decorate());
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
        wmc.add(emailTf);

        // person
        personS2c = new Select2Choice<>("person",
                new PropertyModel<>(model, "person"),
                new Select2PersonProvider(personService));
        personS2c.getSettings().setLanguage(getLocale().getLanguage());
        personS2c.getSettings().setCloseOnSelect(true);
        personS2c.getSettings().setTheme(new Select2BootstrapTheme(true));
        personS2c.getSettings().setMinimumInputLength(3);
        personS2c.getSettings().setPlaceholder(new ResourceModel("select.placeholder", "Please Choose").getObject());
        personS2c.setRequired(true);
        personS2c.setOutputMarkupPlaceholderTag(true);
        personS2c.setEnabled(true);
        personS2c.setLabel(new ResourceModel("person.select", "Select Person"));
        personS2c.add(BootstrapHorizontalFormDecorator.decorate());
        personS2c.add(new ConditionalValidator<Person>(new ResourceModel("person.assign.error", "The person is already assigned to a user")) {
            @Override
            public boolean getCondition(final Person value) {
                return userService.isPersonAssigned(value);
            }
        });
        inner.add(personS2c);

        addBootstrapHorizontalFormDecorator(inner);
    }

    @Override
    protected void onSaveSubmit(final IModel<AddUserDTO> model, final AjaxRequestTarget target) {
        onConfirm(userService.assignOrCreatePersonForUser(model.getObject()), target);
    }

    public IModel<String> getSelectedModel() {
        return selectedModel;
    }

    protected abstract void onConfirm(final Person person, final AjaxRequestTarget target);
}
