package de.kammerchorwernigerode.app.participate.person.presentation.model;

import de.kammerchorwernigerode.app.participate.user.infrastructure.jpa.UserReferenceImpl;

import java.util.Optional;

import lombok.Value;

@Value
public class PersonPickProjectionImpl implements PersonPickProjection {

    Long id;
    String firstName;
    String lastName;
    String fileName;
    String emailAddress;
    UserReferenceImpl user;

    public PersonPickProjectionImpl(PersonPickProjection blueprint) {
        this.id = blueprint.getId();
        this.firstName = blueprint.getFirstName();
        this.lastName = blueprint.getLastName();
        this.fileName = blueprint.getFileName();
        this.emailAddress = blueprint.getEmailAddress();
        this.user = Optional.ofNullable(blueprint.getUser()).map(UserReferenceImpl::new).orElse(null);
    }
}
