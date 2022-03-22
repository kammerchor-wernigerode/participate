package de.vinado.wicket.http;

/**
 * @author Vincent Nadoll
 */
public class BadRequest extends HttpError {

    private static final long serialVersionUID = -3598303085346907509L;

    public BadRequest() {
        super(400);
    }

    public BadRequest(String message) {
        super(message, 400);
    }
}
