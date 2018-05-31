package de.vinado.wicket.participate.data.email;

import java.io.InputStream;
import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EmailAttachment implements Serializable {

    private String name;

    private String mimeType;

    private InputStream inputStream;

    public EmailAttachment(final String name, final String mimeType, final InputStream inputStream) {
        this.name = name;
        this.mimeType = mimeType;
        this.inputStream = inputStream;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
