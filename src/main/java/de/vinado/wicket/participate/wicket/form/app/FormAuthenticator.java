package de.vinado.wicket.participate.wicket.form.app;

/**
 * @author Vincent Nadoll
 */
@FunctionalInterface
public interface FormAuthenticator {

    boolean authenticate(String email, String passwordHash);
}
