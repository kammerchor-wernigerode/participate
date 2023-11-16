package de.vinado.wicket.http;

public class BadRequest extends HttpError {

    public BadRequest(String message) {
        super(message, 400);
    }
}
