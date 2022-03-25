package de.vinado.wicket.participate.wicket.form.app;

import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll
 */
public class FormSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 5348944390546979934L;

    @SpringBean
    private FormAuthenticator authenticator;

    @Getter
    private String email;

    public FormSession(Request request) {
        super(request);

        Injector.get().inject(this);
    }

    @Override
    protected boolean authenticate(String email, String password) {
        String passwordHash = DigestUtils.sha256Hex(password);
        boolean authenticated = authenticator.authenticate(email, passwordHash);

        if (authenticated) {
            this.email = email;
        }

        return authenticated;
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }

    @Override
    public void invalidate() {
        this.email = null;
        super.invalidate();
    }
}
