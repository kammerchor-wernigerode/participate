package de.kammerchorwernigerode.app.participate.user.infrastructure.jpa;

import lombok.Value;

@Value
public class UserReferenceImpl implements UserReference {

    Long id;

    public UserReferenceImpl(UserReference blueprint) {
        this.id = blueprint.getId();
    }
}
