package de.kammerchorwernigerode.app.participate.person.presentation.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PersonDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String fileName;
    private String emailAddress;
}
