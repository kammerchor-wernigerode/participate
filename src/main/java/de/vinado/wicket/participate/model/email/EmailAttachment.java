package de.vinado.wicket.participate.model.email;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Mail attachment wrapper object
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(final String mediaType) {
        this.mediaType = mediaType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
