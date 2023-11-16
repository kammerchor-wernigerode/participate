package de.vinado.wicket.participate.email.service;

/**
 * Defines the accepted types of a multipart mime message.
 *
 * @author Vincent Nadoll
 */
public enum MultipartType {
    PLAIN("plain"),
    HTML("html");

    private final String type;

    MultipartType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
