package de.vinado.wicket.participate.model.dtos;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SendFeedbackDTO implements Serializable {

    private String name;

    private String email;

    private String subject;

    private String message;

    public SendFeedbackDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
