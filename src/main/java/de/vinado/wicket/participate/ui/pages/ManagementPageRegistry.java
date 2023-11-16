package de.vinado.wicket.participate.ui.pages;

import de.vinado.wicket.participate.event.ui.EventSummaryPage;
import de.vinado.wicket.participate.ui.administration.AdminPage;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.login.SignInPage;
import de.vinado.wicket.participate.ui.resetPassword.ResetPasswordPage;
import de.vinado.wicket.participate.ui.singers.SingersPage;
import de.vinado.wicket.participate.wicket.common.PageRegistrar;
import de.vinado.wicket.participate.wicket.common.PageRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.wicket.Page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ManagementPageRegistry implements PageRegistry {

    private static final Map<String, Class<? extends Page>> registrations;

    static {
        registrations = new HashMap<>();

        registrations.put("/login", SignInPage.class);
        registrations.put("/events", EventsPage.class);
        registrations.put("/event/#{event}", EventSummaryPage.class);
        registrations.put("/error/500", ErrorPage.class);
        registrations.put("/error/418", ExpiredPage.class);
        registrations.put("/error/404", PageNotFoundPage.class);
        registrations.put("/singers", SingersPage.class);
        registrations.put("/administration/#{tab}", AdminPage.class);
        registrations.put("/resetPassword", ResetPasswordPage.class);
    }

    @Override
    public Iterator<PageRegistrar> iterator() {
        return registrations.entrySet()
            .stream()
            .map(registration -> new PageRegistrar(registration.getKey(), registration.getValue()))
            .iterator();
    }

    public static ManagementPageRegistry getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ManagementPageRegistry INSTANCE = new ManagementPageRegistry();
    }
}
