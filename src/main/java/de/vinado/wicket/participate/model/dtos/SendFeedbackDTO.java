package de.vinado.wicket.participate.model.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SendFeedbackDTO implements Serializable {

    private String name;
    private String email;
    private String subject;
    private String message;
}
