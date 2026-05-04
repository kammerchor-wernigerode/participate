package de.kammerchorwernigerode.app.participate.wicket.request;

import org.jspecify.annotations.Nullable;

import java.io.Serializable;

import lombok.Value;

@Value
public class ErrorAttributes implements Serializable {

    int statusCode;

    @Nullable
    String message;

    @Nullable
    String path;

    @Nullable
    Throwable throwable;
}
