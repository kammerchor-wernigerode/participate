package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.TextPanel;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.component.Collapsible;
import de.vinado.wicket.participate.component.behavoir.FocusBehavior;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.UserService;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormSignInPage extends BasePage {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    private IModel<MemberToEvent> model;

    private String username;

    private String password;

    private MemberToEvent memberToEvent;

    private boolean rememberMe = false;

    private String participatePassword;

    private String userPassword = "";

    private static final IAuthenticationStrategy strategy = new DefaultAuthenticationStrategy("_form-login", "dke03-sGW3_sxxAsy6_3");

    public FormSignInPage(final IModel<MemberToEvent> model) {
        this(new PageParameters(), model);
    }

    public FormSignInPage(final PageParameters parameters, final IModel<MemberToEvent> model) {
        super(parameters);

        this.model = model;

        setUsername(model.getObject().getMember().getPerson().getEmail());
        setMemberToEvent(model.getObject());

        add(new NotificationPanel("feedback"));

        final FormSignInForm form = new FormSignInForm("form");
        add(form);

        // TODO Password authentication for persons, who are already a user
        final User user = userService.getUser4PersonId(model.getObject().getMember().getPerson().getId());

        this.participatePassword = ParticipateApplication.get().getApplicationProperties().getParticipatePassword();
        if (null != user) {
            this.userPassword = user.getPasswordSha256();
        }

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("usage")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new TextPanel(panelId, new ResourceModel("formUsageDescription"));
            }
        });

        add(new Collapsible("collapsible", tabs) {
            @Override
            protected CssClassNameAppender getActiveCssClassNameAppender() {
                return new CssClassNameAppender("");
            }
        });
    }

    private class FormSignInForm extends StatelessForm<FormSignInPage> {

        FormSignInForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<>(FormSignInPage.this));

            final TextField<String> usernameTf = new TextField<>("username");
            usernameTf.setEnabled(false);
            add(usernameTf);

            final PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.add(new AttributeModifier("placeholder", new ResourceModel("password", "Password")));
            passwordTf.add(new FocusBehavior());
            passwordTf.setRequired(false);
            add(passwordTf);

            add(new CheckBox("rememberMe"));

            final DropDownChoice<MemberToEvent> eventDdc = new DropDownChoice<>("memberToEvent",
                eventService.getMemberToEventList(model.getObject().getMember()), new ChoiceRenderer<>("event.name"));
            eventDdc.setLabel(new ResourceModel("event", "Event"));
            add(eventDdc);

            final BootstrapAjaxButton declineBtn = new BootstrapAjaxButton("declineBtn",
                new ResourceModel("decline", "Decline"), this, Buttons.Type.Default) {
                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (signIn(getPassword())) {
                        onDecline();
                    }
                }
            };
            add(declineBtn);
        }

        @Override
        protected void onSubmit() {
            if (signIn(getPassword())) {
                if (getRememberMe()) {
                    strategy.save(memberToEvent.getToken(), password);
                } else {
                    strategy.remove();
                }
                onAccept(getModel().getObject().getMemberToEvent());
            } else {
                onSignInFailed();
                strategy.remove();
            }
        }
    }

    private boolean signIn(final String password) {
        if (!Strings.isEmpty(password)) {
            return password.equals(participatePassword) || password.equals(userPassword);
        }
        return false;
    }

    private void onSignInFailed() {
        error(getLocalizer().getString("signInFailed", this, "Anmeldung fehlgeschlagen"));
    }

    private void onAccept(final MemberToEvent memberToEvent) {
        setResponsePage(new FormPage(new PageParameters().clearNamed().clearIndexed()
            .add("token", memberToEvent.getToken()), new CompoundPropertyModel<>(memberToEvent), true));
    }

    private void onDecline() {
        eventService.declineEvent(model.getObject());
    }

    @Override
    protected void onConfigure() {
        final String[] data = strategy.load();
        if ((null != data) && (data.length > 1)) {
            if (signIn(data[1])) {
                final String token = data[0];
                password = data[1];

                final Member cookieMember = eventService.getMemberToEvent(token).getMember();
                final Member member = model.getObject().getMember();
                if (member.equals(cookieMember)) {
                    final Event event = model.getObject().getEvent();
                    if (event.isActive() && event.getEndDate().after(new Date())) {
                        onAccept(model.getObject());
                    } else {
                        final MemberToEvent latest = eventService.getLatestMemberToEvent(model.getObject().getMember());
                        if (null != latest) {
                            strategy.save(latest.getToken(), password);
                            onAccept(latest);
                        }
                    }
                } else {
                    strategy.remove();
                }
            } else {
                strategy.remove();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public MemberToEvent getMemberToEvent() {
        return memberToEvent;
    }

    public void setMemberToEvent(final MemberToEvent memberToEvent) {
        this.memberToEvent = memberToEvent;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
