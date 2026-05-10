package de.kammerchorwernigerode.app.participate.person.presentation.model;

import org.springframework.util.StringUtils;

import java.io.Serializable;

public interface PersonProjection extends Serializable {

    String getFirstName();

    String getLastName();

    String getFileName();

    default String getDisplayName() {
        String fileName = getFileName();
        if (StringUtils.hasText(fileName)) {
            return fileName;
        }

        String lastName = getLastName();
        String firstName = getFirstName();
        return firstName + " " + lastName;

    }
}
