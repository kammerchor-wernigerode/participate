package de.vinado.wicket.participate.model.email;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Mail attachment wrapper object
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @deprecated use {@link de.vinado.wicket.participate.email.EmailAttachment} instead
 */
@Getter
@Setter
@Deprecated
public class EmailAttachment implements Serializable {

    private String name;
    private String mediaType;
    private InputStream inputStream;

    /**
     * Construct. All parameters must not be null.
     *
     * @param name        Attachment name
     * @param mediaType   Attachment's MimeType
     * @param inputStream Attachment content
     */
    public EmailAttachment(final String name, final String mediaType, final InputStream inputStream) {
        this.name = name;
        this.mediaType = mediaType;
        this.inputStream = inputStream;
    }
}
