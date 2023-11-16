package de.vinado.wicket.participate.wicket.form.app;

import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class FormSession extends AuthenticatedWebSession {

    @SpringBean
    private FormAuthenticator authenticator;

    @SpringBean
    private EventService eventService;

    @Getter
    private String email;
    private final AtomicReference<String> token;

    public FormSession(Request request) {
        super(request);

        this.token = Optional.of(request)
            .map(Request::getQueryParameters)
            .map(extract("token"))
            .map(StringValue::toOptionalString)
            .map(AtomicReference::new)
            .orElseGet(AtomicReference::new);

        Injector.get().inject(this);
    }

    private static Function<IRequestParameters, StringValue> extract(String name) {
        return parameters -> parameters.getParameterValue(name);
    }

    public static FormSession get() {
        return (FormSession) Session.get();
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

    protected boolean authorize() {
        return Optional.of(token)
            .map(AtomicReference::get)
            .map(eventService::getParticipant)
            .map(Participant::getSinger)
            .map(Singer::getEmail)
            .filter(matching(email))
            .isPresent();
    }

    private static Predicate<String> matching(String other) {
        return self -> Objects.equals(self, other);
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }

    @Override
    public void invalidate() {
        this.email = null;
        this.token.set(null);
        super.invalidate();
    }

    public String getToken() {
        return token.get();
    }

    public void setToken(String token) {
        this.token.set(token);
    }
}
