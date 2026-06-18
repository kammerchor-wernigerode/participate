package de.kammerchorwernigerode.app.participate.person.presentation.model;

import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserReference;

public interface PersonPickProjection extends PersonProjection {

    String getEmailAddress();

    UserReference getUser();

    default boolean hasUser() {
        return null != getUser();
    }
}
