package de.vinado.wicket.participate.ui.pages;

import de.vinado.wicket.participate.ui.administration.AdminPage;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.form.FormPage;
import de.vinado.wicket.participate.ui.form.FormSignInPage;
import de.vinado.wicket.participate.ui.login.SignInPage;
import de.vinado.wicket.participate.ui.resetPassword.ResetPasswordPage;
import de.vinado.wicket.participate.ui.singers.SingersPage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.wicket.Page;
import org.springframework.data.util.Streamable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Vincent Nadoll
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageRegistry implements Streamable<PageRegistrar> {

    private static final Map<String, Class<? extends Page>> registrations;

    static {
        registrations = new HashMap<>();

        registrations.put("/login", SignInPage.class);
        registrations.put("/events", EventsPage.class);
        registrations.put("/error/500", ErrorPage.class);
        registrations.put("/error/418", ExpiredPage.class);
        registrations.put("/error/404", PageNotFoundPage.class);
        registrations.put("/singers", SingersPage.class);
        registrations.put("/administration", AdminPage.class);
        registrations.put("/participate", FormPage.class);
        registrations.put("/participate/login", FormSignInPage.class);
        registrations.put("/resetPassword", ResetPasswordPage.class);
    }

    @Override
    public Iterator<PageRegistrar> iterator() {
        return registrations.entrySet()
            .stream()
            .map(registration -> new PageRegistrar(registration.getKey(), registration.getValue()))
            .iterator();
    }

    public static PageRegistry getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final PageRegistry INSTANCE = new PageRegistry();
    }
}
