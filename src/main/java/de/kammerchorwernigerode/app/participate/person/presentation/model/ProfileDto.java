package de.kammerchorwernigerode.app.participate.person.presentation.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProfileDto implements Serializable {

    private PersonDto model = new PersonDto();
    private PersonPickProjection instance;
}
