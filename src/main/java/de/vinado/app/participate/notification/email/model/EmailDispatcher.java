package de.vinado.app.participate.notification.email.model;

@FunctionalInterface
public interface EmailDispatcher {

    void dispatch(Email email, Transmission... transmissions) throws EmailException;
}
