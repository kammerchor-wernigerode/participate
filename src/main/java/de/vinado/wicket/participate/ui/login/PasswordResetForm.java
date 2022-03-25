package de.vinado.wicket.participate.ui.login;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.bt4.button.BootstrapAjaxButton;
import de.vinado.wicket.participate.services.UserService;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll
 */
public class PasswordResetForm extends StatelessForm<PasswordResetForm> {

    @SpringBean
    private UserService userService;

    @Getter
    @Setter
    private String email;
    private FeedbackPanel feedback;

    public PasswordResetForm(String id) {
        super(id);

        setModel(new CompoundPropertyModel<>(PasswordResetForm.this));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(emailField("email"));

        Button submitButton;
        add(submitButton = submitButton("submit"));
        setDefaultButton(submitButton);

        add(feedback = alertPanel("feedback"));
        feedback.setOutputMarkupId(true);
    }

    private FormComponent<String> emailField(String id) {
        return new EmailTextField(id)
            .setLabel(new ResourceModel("email", "Email"))
            .setRequired(true);
    }

    private Button submitButton(String id) {
        return new BootstrapAjaxButton(id, Buttons.Type.Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                super.onSubmit(target);
                PasswordResetForm.this.onSubmit(target);
            }
        }
            .setLabel(new ResourceModel("password.reset", "Reset Password"));
    }

    private FeedbackPanel alertPanel(String id) {
        return new NotificationPanel(id, new ContainerFeedbackMessageFilter(PasswordResetForm.this)) {
            @Override
            protected Component newMessageDisplayComponent(String markupId, FeedbackMessage message) {
                Component component = super.newMessageDisplayComponent(markupId, message);
                component.add(new CssClassNameAppender("mt-3"));
                return component;
            }
        };
    }

    private void onSubmit(AjaxRequestTarget target) {
        try {
            userService.startPasswordReset(email, false);
            success(getLocalizer().getString("password.reset.success", this, "An email has been sent. Check your inbox."));
        } catch (Exception e) {
            success(getLocalizer().getString("email.send.error", this, "There was an error while sending the email"));
        }

        target.add(feedback);
    }
}
