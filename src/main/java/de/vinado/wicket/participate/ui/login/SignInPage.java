package de.vinado.wicket.participate.ui.login;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.participate.components.panels.Collapsible;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.service.UserService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SignInPage extends BasePage {

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    public SignInPage(final PageParameters parameters) {
        super(parameters);

        add(new SignInPanel("signInPanel"));

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("resetPasswordQ", "Forgot Password?")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new ResetPasswordPanel(panelId, new CompoundPropertyModel<>(new ResetPassword()));
            }
        });

        add(new Collapsible("collapsible", tabs) {
            @Override
            protected CssClassNameAppender getActiveCssClassNameAppender() {
                return new CssClassNameAppender("");
            }
        });
    }

    private class ResetPasswordPanel extends Panel {

        ResetPasswordPanel(final String id, final IModel<ResetPassword> model) {
            super(id, model);

            final Form form = new Form("form");
            add(form);

            final EmailTextField emailTf = new EmailTextField("email");
            form.add(emailTf);

            final AjaxSubmitLink submitBtn = new AjaxSubmitLink("submit") {
                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> inner) {
                    if (Strings.isEmpty(model.getObject().getEmail())) {
                        Snackbar.show(target, new ResourceModel("password.reset.email", "Enter your email address"));
                        return;
                    }
                    if (userService.startPasswordReset(model.getObject().getEmail(), false)) {
                        Snackbar.show(target, new ResourceModel("password.reset.success", "An email has been sent. Check your inbox."));
                    } else {
                        Snackbar.show(target, new ResourceModel("email.send.error", "There was an error while sending the email"));
                    }
                }
            };
            form.add(submitBtn);
        }
    }

    private class ResetPassword implements Serializable {

        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(final String email) {
            this.email = email;
        }
    }
}
