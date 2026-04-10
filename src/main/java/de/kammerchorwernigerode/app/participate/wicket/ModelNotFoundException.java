package de.kammerchorwernigerode.app.participate.wicket;

import org.apache.wicket.WicketRuntimeException;

public class ModelNotFoundException extends WicketRuntimeException {

    public ModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
