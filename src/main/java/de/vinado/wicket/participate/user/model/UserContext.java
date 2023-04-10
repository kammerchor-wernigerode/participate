package de.vinado.wicket.participate.user.model;

import de.vinado.wicket.participate.model.User;

@FunctionalInterface
public interface UserContext {

    User get();
}
