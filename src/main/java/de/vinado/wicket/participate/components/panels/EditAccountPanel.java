package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.EditAccountDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.dtos.SingerDTO;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidationError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EditAccountPanel extends GenericPanel<EditAccountDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private final Form<EditAccountDTO> form;

    public EditAccountPanel(String id, IModel<EditAccountDTO> model) {
        super(id, model);

        this.form = form("form");
    }

    protected Form<EditAccountDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                EditAccountPanel.this.onSubmit();
            }
        };
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(form);

        List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("account.user", "User Account")) {
            @Override
            public Panel getPanel(String panelId) {
                return new EditUserPanel(panelId, getModel());
            }
        });
        if (null != getModelObject().getPerson()) {
            tabs.add(new AbstractTab(new ResourceModel("account.personal-details", "Personal Details")) {
                @Override
                public Panel getPanel(String panelId) {
                    return new EditPersonPanel(panelId, getModel());
                }
            });
        }

        AjaxBootstrapTabbedPanel tabbedPanel = new AjaxBootstrapTabbedPanel<>("tabs", tabs);
        form.add(tabbedPanel);
    }

    protected void onSubmit() {
        EditAccountDTO modelObject = getModelObject();

        if (null != modelObject.getPerson()) {
            PersonDTO personDTO = new PersonDTO(modelObject.getPerson());
            personDTO.setFirstName(modelObject.getFirstName());
            personDTO.setLastName(modelObject.getLastName());
            personDTO.setEmail(modelObject.getEmail());
            personService.savePerson(personDTO);
            if (null != modelObject.getSinger()) {
                SingerDTO singerDTO = new SingerDTO(modelObject.getSinger());
                singerDTO.setVoice(modelObject.getVoice());
                personService.saveSinger(singerDTO);
            }
        }

        AddUserDTO userDTO = new AddUserDTO(modelObject.getUser());
        userDTO.setUsername(modelObject.getUsername());
        userDTO.setPassword(modelObject.getPassword());
        User user = userService.saveUser(userDTO);
        modelObject.setUser(user);
    }


    private class EditUserPanel extends Panel {

        public EditUserPanel(String id, IModel<EditAccountDTO> model) {
            super(id, model);

            TextField usernameTf = new TextField("username");
            usernameTf.setEnabled(false);
            usernameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(usernameTf, new FormComponentLabel("usernameLabel", usernameTf));

            PasswordTextField oldPasswordTf = new PasswordTextField("oldPassword");
            oldPasswordTf.setLabel(new ResourceModel("oldPassword", "Old Password"));
            oldPasswordTf.setRequired(false);
            oldPasswordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(oldPasswordTf, new FormComponentLabel("oldPasswordLabel", oldPasswordTf));

            PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.setRequired(false);
            passwordTf.setLabel(new ResourceModel("password", "Password"));
            passwordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(passwordTf, new FormComponentLabel("passwordLabel", passwordTf));

            PasswordTextField confirmPasswordTf = new PasswordTextField("confirmPassword");
            confirmPasswordTf.setRequired(false);
            confirmPasswordTf.setLabel(new ResourceModel("confirmPassword", "Confirm Password"));
            confirmPasswordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(confirmPasswordTf, new FormComponentLabel("confirmPasswordLabel", confirmPasswordTf));

            form.add(new EqualPasswordInputValidator(passwordTf, confirmPasswordTf));
            form.add(new AbstractFormValidator() {
                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[0];
                }

                @Override
                public void validate(Form<?> form) {
                    if (!Strings.isEmpty(passwordTf.getConvertedInput())) {
                        if (!Strings.isEmpty(oldPasswordTf.getConvertedInput())) {
                            oldPasswordTf.error((IValidationError) messageSource -> new ResourceModel("account.password.error", "Your input does not match your current password"));
                        } else if (!model.getObject().getUser().getPasswordSha256().equals(DigestUtils.sha256Hex(oldPasswordTf.getConvertedInput()))) {
                            oldPasswordTf.error((IValidationError) messageSource -> new ResourceModel("account.password.error", "Your input does not match your current password"));
                        }
                    }
                }
            });
        }
    }

    private class EditPersonPanel extends Panel {

        public EditPersonPanel(String id, IModel<EditAccountDTO> model) {
            super(id, model);

            TextField firstNameTf = new TextField("firstName");
            firstNameTf.setLabel(new ResourceModel("firstName", "Given Name"));
            firstNameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(firstNameTf, new FormComponentLabel("firstNameLabel", firstNameTf));

            TextField lastNameTf = new TextField("lastName");
            lastNameTf.setLabel(new ResourceModel("lastName", "Surname"));
            lastNameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(lastNameTf, new FormComponentLabel("lastNameLabel", lastNameTf));

            EmailTextField emailTf = new EmailTextField("email");
            emailTf.setLabel(new ResourceModel("email", "Email"));
            emailTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(emailTf, new FormComponentLabel("emailLabel", emailTf));

            DropDownChoice<Voice> voiceDd = new DropDownChoice<Voice>("voice",
                Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>()) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(null != model.getObject().getSinger());
                }
            };
            voiceDd.setLabel(new ResourceModel("voice", "Voice"));
            voiceDd.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                }
            });
            add(voiceDd, new FormComponentLabel("voiceLabel", voiceDd));
        }
    }
}
