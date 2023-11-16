package de.vinado.wicket.http;

import lombok.Getter;
import org.apache.wicket.WicketRuntimeException;

public abstract class HttpError extends WicketRuntimeException {

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
