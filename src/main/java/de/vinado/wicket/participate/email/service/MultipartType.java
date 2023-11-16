package de.vinado.wicket.participate.email.service;

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
