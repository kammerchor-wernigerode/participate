package de.vinado.wicket.participate.wicket.form.app;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;

/**
 * @author Vincent Nadoll
 */
public class FormSession extends AuthenticatedWebSession {

    private static final long serialVersionUID = 5348944390546979934L;

    public FormSession(Request request) {
        super(request);

        Injector.get().inject(this);
    }

    @Override
    protected boolean authenticate(String email, String password) {
        return true;
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }
}
