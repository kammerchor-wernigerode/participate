package de.vinado.wicket.participate.ui.resetPassword;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.validator.StringValidator;

import javax.persistence.NoResultException;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class ResetPasswordPanel extends Panel {

    private static final String RESET_PASSWORD_FORM = "resetPasswordForm";

    private String recoveryToken;

    private String password;

    private String confirmPassword;

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    public ResetPasswordPanel(final String id, final String recoveryToken) {
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

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    private class ResetPasswordForm extends StatelessForm<ResetPasswordPanel> {

        public ResetPasswordForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<>(ResetPasswordPanel.this));

            final PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.setRequired(true);
            passwordTf.add(StringValidator.minimumLength(6));
            add(passwordTf);

            final PasswordTextField confirmPasswordTf = new PasswordTextField("confirmPassword");
            confirmPasswordTf.setRequired(true);
            add(confirmPasswordTf);

            add(new EqualPasswordInputValidator(passwordTf, confirmPasswordTf));
        }

        @Override
        protected void onSubmit() {
            try {
                IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
                userService.finishPasswordReset(getRecoveryToken(), getPassword(),
                    getLocalizer().getString("account.reset.success", this, "Your password reset succeeded"));
                strategy.remove();
                getRequestCycle().setResponsePage(ParticipateApplication.get().getHomePage());
            } catch (NoResultException e) {
                log.warn("Failed to finish password recovery for user recovery token={}", getRecoveryToken());
                AjaxRequestTarget target = getRequestCycle().find(AjaxRequestTarget.class);
                Snackbar.show(target, "Your recovery token is invalid");
            }
        }
    }
}
