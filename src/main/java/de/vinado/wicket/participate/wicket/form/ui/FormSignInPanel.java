package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll
 */
@Getter
@Setter
public class FormSignInPanel extends Panel {

    private static final long serialVersionUID = 8628206186642582897L;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @SpringBean
    private EventService eventService;

    private Participant participant;
    private String email;
    private String token;
    private String password;
    private boolean rememberMe;

    public FormSignInPanel(String id, IModel<Participant> model) {
        super(id);

        Participant participant = model.getObject();
        this.participant = participant;
        this.email = participant.getSinger().getEmail();
        this.token = participant.getToken();
    }

    @Override
    protected void onConfigure() {
        if (isSignedIn()) {
            super.onConfigure();
            return;
        }

        IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
        String[] data = strategy.load();
        if ((data != null) && (data.length > 1)) {
            if (signIn(data[0], data[1])) {
                email = data[0];
                password = data[1];

                onSignInRemembered();
            } else {
                strategy.remove();
            }
        }

        super.onConfigure();
    }

    private boolean isSignedIn() {
        return AuthenticatedWebSession.get().isSignedIn();
    }

    private boolean signIn(String email, String password) {
        return AuthenticatedWebSession.get().signIn(email, password);
    }

    private void onSignInRemembered() {
        continueToOriginalDestination();

        PageParameters pageParameters = encodeParticipant();
        throw new RestartResponseException(getApplication().getHomePage(), pageParameters);
    }

    private void onSignInSucceeded() {
        continueToOriginalDestination();

        PageParameters pageParameters = encodeParticipant();
        setResponsePage(getApplication().getHomePage(), pageParameters);
    }

    private PageParameters encodeParticipant() {
        PageParameters pageParameters = new PageParameters(getPage().getPageParameters());
        pageParameters.set("token", participant.getToken());
        return pageParameters;
    }

    private void onSignInFailed() {
        error(getLocalizer().getString("login.error", this, "Sign in failed"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(feedback());
        add(form());
    }

    private Component feedback() {
        return new NotificationPanel("feedback", this) {

            private static final long serialVersionUID = 4302364850707994223L;

            @Override
            protected Component newMessageDisplayComponent(String markupId, FeedbackMessage message) {
                Component component = super.newMessageDisplayComponent(markupId, message);
                component.add(new CssClassNameAppender("mt-3"));
                return component;
            }
        };
    }

    private Component form() {
        return new SignInForm("form");
    }


    private final class SignInForm extends StatelessForm<FormSignInPanel> {

        private static final long serialVersionUID = 7276726225898679025L;

        public SignInForm(String id) {
            super(id);

            setModel(new CompoundPropertyModel<>(FormSignInPanel.this));
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(email());
            add(password());
            add(rememberMe());
            add(participant());
        }

        private Component[] email() {
            Component emailAddon = new WebMarkupContainer("emailAddon")
                .setOutputMarkupId(true);

            Component email = new EmailTextField("email")
                .setRequired(true)
                .setEnabled(false)
                .add(AttributeAppender.replace("aria-describedby", emailAddon.getMarkupId()));

            return new Component[]{emailAddon, email};
        }

        private Component[] password() {
            Component passwordAddon = new WebMarkupContainer("passwordAddon")
                .setOutputMarkupId(true);

            Component password = new PasswordTextField("password");

            return new Component[]{passwordAddon, password};
        }

        private Component[] rememberMe() {
            FormComponent<Boolean> rememberMe = new CheckBox("rememberMe")
                .setLabel(new ResourceModel("rememberMe", "Remember Me"));

            SimpleFormComponentLabel rememberMeLabel = new SimpleFormComponentLabel("rememberMeLabel", rememberMe);

            Component rememberMeContainer;
            add(rememberMeContainer = new WebMarkupContainer("rememberMeWmc")
                .add(rememberMe, rememberMeLabel)
                .setVisible(false)
                .setOutputMarkupPlaceholderTag(true));

            return new Component[]{rememberMeContainer};
        }

        private Component[] participant() {
            IModel<List<Participant>> choices = getModel().map(FormSignInPanel::getParticipant)
                .map(Participant::getSinger)
                .map(eventService::getParticipants);
            FormComponent<Participant> participant = new DropDownChoice<>("participant", choices,
                new ChoiceRenderer<>("event.name"))
                .setLabel(new ResourceModel("event", "Event"));

            return new Component[]{participant};
        }

        @Override
        protected void onSubmit() {
            super.onSubmit();

            IAuthenticationStrategy strategy = getApplication().getSecuritySettings().getAuthenticationStrategy();
            if (signIn(getEmail(), getPassword())) {
                if (rememberMe) {
                    strategy.save(email, password);
                } else {
                    strategy.remove();
                }

                onSignInSucceeded();
            } else {
                onSignInFailed();
                strategy.remove();
            }
        }
    }
}
