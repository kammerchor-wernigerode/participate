package de.vinado.wicket.participate.components.panels;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
import de.vinado.wicket.participate.model.Singer;
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

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class EditAccountPanel extends BootstrapModalPanel<EditAccountDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    public EditAccountPanel(final BootstrapModal modal, final IModel<EditAccountDTO> model) {
        super(modal, new ResourceModel("account.edit", "Edit Account"), model);

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("account.user", "User Account")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new EditUserPanel(panelId, model);
            }
        });
        if (null != model.getObject().getPerson()) {
            tabs.add(new AbstractTab(new ResourceModel("account.personal-details", "Personal Details")) {
                @Override
                public Panel getPanel(final String panelId) {
                    return new EditPersonPanel(panelId, model);
                }
            });
        }
        /*tabs.add(new AbstractTab(Model.of("Informationen")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new EditPersonInformationPanel(panelId, model);
            }
        });*/

        final AjaxBootstrapTabbedPanel tabbedPanel = new AjaxBootstrapTabbedPanel<>("tabs", tabs);
        inner.add(tabbedPanel);
    }

    @Override
    protected void onSaveSubmit(final IModel<EditAccountDTO> model, final AjaxRequestTarget target) {
        final EditAccountDTO modelObject = model.getObject();

        if (null != modelObject.getSinger()) {
            final PersonDTO personDTO = new PersonDTO(modelObject.getPerson());
            personDTO.setFirstName(modelObject.getFirstName());
            personDTO.setLastName(modelObject.getLastName());
            personDTO.setEmail(modelObject.getEmail());
            personService.savePerson(personDTO);
            if (null != modelObject.getSinger()) {
                final Singer singer = modelObject.getSinger();
                final SingerDTO singerDTO = new SingerDTO();
                singerDTO.setVoice(modelObject.getVoice());
                personService.saveSinger(singerDTO);
            }
        }

        final AddUserDTO userDTO = new AddUserDTO(modelObject.getUser());
        userDTO.setUsername(modelObject.getUsername());
        userDTO.setPassword(modelObject.getPassword());
        onConfirm(userService.saveUser(userDTO), target);
    }

    protected abstract void onConfirm(final User user, final AjaxRequestTarget target);

    private class EditUserPanel extends Panel {

        public EditUserPanel(final String id, final IModel<EditAccountDTO> model) {
            super(id, model);

            final TextField usernameTf = new TextField("username");
            usernameTf.add(BootstrapHorizontalFormDecorator.decorate());
            usernameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(usernameTf);

            final PasswordTextField oldPasswordTf = new PasswordTextField("oldPassword");
            oldPasswordTf.add(BootstrapHorizontalFormDecorator.decorate());
            oldPasswordTf.setRequired(false);
            oldPasswordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(oldPasswordTf);

            final PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.setRequired(false);
            passwordTf.add(BootstrapHorizontalFormDecorator.decorate());
            passwordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(passwordTf);

            final PasswordTextField confirmPasswordTf = new PasswordTextField("confirmPassword");
            confirmPasswordTf.setRequired(false);
            confirmPasswordTf.add(BootstrapHorizontalFormDecorator.decorate());
            confirmPasswordTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(confirmPasswordTf);

            inner.add(new EqualPasswordInputValidator(passwordTf, confirmPasswordTf));
            inner.add(new AbstractFormValidator() {
                @Override
                public FormComponent<?>[] getDependentFormComponents() {
                    return new FormComponent[0];
                }

                @Override
                public void validate(final Form<?> form) {
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

        public EditPersonPanel(final String id, final IModel<EditAccountDTO> model) {
            super(id, model);

            final TextField firstNameTf = new TextField("firstName");
            firstNameTf.add(BootstrapHorizontalFormDecorator.decorate());
            firstNameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(firstNameTf);

            final TextField lastNameTf = new TextField("lastName");
            lastNameTf.add(BootstrapHorizontalFormDecorator.decorate());
            lastNameTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(lastNameTf);

            final EmailTextField emailTf = new EmailTextField("email");
            emailTf.add(BootstrapHorizontalFormDecorator.decorate());
            emailTf.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(emailTf);

            final DropDownChoice<Voice> voiceDd = new DropDownChoice<Voice>("voice",
                Collections.unmodifiableList(Arrays.asList(Voice.values())), new EnumChoiceRenderer<>()) {
                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setVisible(null != model.getObject().getSinger());
                }
            };
            voiceDd.add(BootstrapHorizontalFormDecorator.decorate());
            voiceDd.add(new AjaxFormComponentUpdatingBehavior("change") {
                @Override
                protected void onUpdate(final AjaxRequestTarget target) {
                }
            });
            add(voiceDd);
        }
    }

    private class EditPersonInformationPanel extends Panel {

        public EditPersonInformationPanel(final String id, final IModel<EditAccountDTO> model) {
            super(id, model);


        }
    }
}
