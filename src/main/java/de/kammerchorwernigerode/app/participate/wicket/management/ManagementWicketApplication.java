package de.kammerchorwernigerode.app.participate.wicket.management;

import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.EventsPage;
import de.kammerchorwernigerode.app.participate.wicket.WicketApplication;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.settings.SecuritySettings;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManagementWicketApplication extends WicketApplication
    implements IRoleCheckingStrategy, IUnauthorizedComponentInstantiationListener {

    private final Environment environment;
    private final EventRecordRepository eventRecordRepository;

    @Override
    public Class<? extends Page> getHomePage() {
        return EventsPage.class;
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new ManagementWicketSession(request, environment, eventRecordRepository);
    }

    @Override
    protected void init() {
        super.init();

        SecuritySettings securitySettings = getSecuritySettings();
        configure(securitySettings);

        WebjarsSettings webjarsSettings = getWebjarsSettings();
        WicketWebjars.install(this, webjarsSettings);
    }

    protected void configure(SecuritySettings settings) {
        settings.setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this::authorized));
    }

    private boolean authorized(Roles requiredRoles) {
        AbstractAuthenticatedWebSession session = AbstractAuthenticatedWebSession.get();
        Roles grantedRoles = session.getRoles();
        return grantedRoles.hasAnyRole(requiredRoles);
    }

    @Override
    public final boolean hasAnyRole(Roles roles) {
        AbstractAuthenticatedWebSession session = AbstractAuthenticatedWebSession.get();
        Roles sessionRoles = session.getRoles();
        return (sessionRoles != null) && sessionRoles.hasAnyRole(roles);
    }

    @Override
    public void onUnauthorizedInstantiation(Component component) {
        throw new UnauthorizedInstantiationException(component.getClass());
    }
}
