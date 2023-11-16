package de.vinado.wicket.participate.wicket.form.app;

@FunctionalInterface
public interface FormAuthenticator {

    boolean authenticate(String email, String passwordHash);
}
