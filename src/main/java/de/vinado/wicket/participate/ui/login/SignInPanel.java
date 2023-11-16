package de.vinado.wicket.participate.ui.login;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.common.FocusBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;

public class SignInPanel extends org.apache.wicket.authroles.authentication.panel.SignInPanel {

    public SignInPanel(String id) {
        super(id);

        setRememberMe(false);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addOrReplace(alertPanel("feedback"));
        getForm().get("username").add(new FocusBehavior());
    }

    private Component alertPanel(String id) {
        return new NotificationPanel(id, this) {
            @Override
            protected Component newMessageDisplayComponent(String markupId, FeedbackMessage message) {
                Component component = super.newMessageDisplayComponent(markupId, message);
                component.add(new CssClassNameAppender("mt-3"));
                return component;
            }
        };
    }
}
