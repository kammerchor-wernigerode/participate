package de.vinado.wicket.participate.ui.resetPassword;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.Application;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

public class ResetPasswordPanel extends Panel {

    private static final String RESET_PASSWORD_FORM = "resetPasswordForm";

    private String recoveryToken;

    private String password;

    private String confirmPassword;

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    public ResetPasswordPanel(String id, String recoveryToken) {
        super(id);
        this.recoveryToken = recoveryToken;

        add(new NotificationPanel("feedback"));

        add(new ResetPasswordForm(RESET_PASSWORD_FORM));
    }

    public String getRecoveryToken() {
        return recoveryToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    private boolean resetPassword(String recoveryToken, String password) {
        return userService.finishPasswordReset(recoveryToken, password);
    }

    private class ResetPasswordForm extends StatelessForm<ResetPasswordPanel> {

        public ResetPasswordForm(String id) {
            super(id);

            setModel(new CompoundPropertyModel<>(ResetPasswordPanel.this));

            PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.setRequired(true);
            passwordTf.add(StringValidator.minimumLength(6));
            add(passwordTf);

            PasswordTextField confirmPasswordTf = new PasswordTextField("confirmPassword");
            confirmPasswordTf.setRequired(true);
            add(confirmPasswordTf);

            add(new EqualPasswordInputValidator(passwordTf, confirmPasswordTf));
        }

        @Override
        protected void onSubmit() {
            IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();

            if (resetPassword(getRecoveryToken(), getPassword())) {
                strategy.remove();
                getRequestCycle().setResponsePage(Application.get().getHomePage());
            }
        }
    }
}
