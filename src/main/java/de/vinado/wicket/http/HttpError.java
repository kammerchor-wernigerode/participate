package de.vinado.wicket.http;

import lombok.Getter;
import org.apache.wicket.WicketRuntimeException;

/**
 * @author Vincent Nadoll
 */
public abstract class HttpError extends WicketRuntimeException {

    private static final long serialVersionUID = -2485604828307287351L;

    @Getter
    private final int status;

    protected HttpError(int status) {
        this.status = status;
    }

    public HttpError(String message, int status) {
        super(message);
        this.status = status;
    }
}
