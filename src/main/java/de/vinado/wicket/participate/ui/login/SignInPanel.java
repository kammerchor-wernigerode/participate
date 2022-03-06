/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.vinado.wicket.participate.ui.login;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.vinado.wicket.participate.behavoirs.FocusBehavior;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SignInPanel extends Panel {

    private static final String SIGN_IN_FORM = "signInForm";

    private boolean rememberMe = false;

    private String password;

    private String username;

    /**
     * @param id                See Component constructor
     * @see org.apache.wicket.Component#Component(String)
     */
    public SignInPanel(final String id) {
        super(id);

        add(new NotificationPanel("feedback"));
        add(new SignInForm(SIGN_IN_FORM));
    }

    /**
     * @return signin form
     */
    protected SignInForm getForm() {
        return (SignInForm) get(SIGN_IN_FORM);
    }

    /**
     * Try to sign-in with remembered credentials.
     *
     * @see #setRememberMe(boolean)
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (!isSignedIn()) {
            final IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();

            final String[] data = strategy.load();

            if ((data != null) && (data.length > 1)) {

                if (signIn(data[0], data[1])) {
                    username = data[0];
                    password = data[1];

                    onSignInRemembered();
                } else {
                    strategy.remove();
                }
            }
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(final boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    /**
     * Sign in user if possible.
     *
     * @param username The username
     * @param password The password
     * @return True if signin was successful
     */
    private boolean signIn(final String username, final String password) {
        return AuthenticatedWebSession.get().signIn(username, password);
    }

    /**
     * @return true, if signed in
     */
    private boolean isSignedIn() {
        return AuthenticatedWebSession.get().isSignedIn();
    }

    /**
     * Called when sign in failed
     */
    protected void onSignInFailed() {
        error(getLocalizer().getString("signInFailed", this, "Anmeldung fehlgeschlagen"));
    }

    /**
     * Called when sign in was successful
     */
    protected void onSignInSucceeded() {
        continueToOriginalDestination();
        setResponsePage(getApplication().getHomePage());
    }

    /**
     * Called when sign-in was remembered.
     * <p>
     * By default tries to continue to the original destination or switches to the application's
     * home page.
     * <p>
     * Note: This method will be called during rendering of this panel, thus a
     * {@link RestartResponseException} has to be used to switch to a different page.
     *
     * @see #onConfigure()
     */
    protected void onSignInRemembered() {
        continueToOriginalDestination();

        throw new RestartResponseException(getApplication().getHomePage());
    }

    /**
     * Sign in form.
     */
    public final class SignInForm extends StatelessForm<SignInPanel> {

        /**
         * Constructor.
         *
         * @param id id of the form component
         */
        public SignInForm(final String id) {
            super(id);

            setModel(new CompoundPropertyModel<>(SignInPanel.this));

            final TextField usernameTf = new RequiredTextField("username");
            usernameTf.setLabel(new ResourceModel("username-email", "Username/Email"));
            usernameTf.add(new AttributeModifier("placeholder", new ResourceModel("username", "Username")));
            usernameTf.add(new FocusBehavior());
            add(usernameTf);

            final PasswordTextField passwordTf = new PasswordTextField("password");
            passwordTf.add(new AttributeModifier("placeholder", new ResourceModel("password", "Password")));
            add(passwordTf);

            add(new CheckBox("rememberMe"));
        }

        /**
         * @see org.apache.wicket.markup.html.form.Form#onSubmit()
         */
        @Override
        public final void onSubmit() {
            final IAuthenticationStrategy strategy = getApplication().getSecuritySettings()
                    .getAuthenticationStrategy();

            if (signIn(getUsername(), getPassword())) {
                if (getRememberMe()) {
                    strategy.save(username, password);
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
