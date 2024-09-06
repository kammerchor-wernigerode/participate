package de.vinado.app.participate.notification.email.support;

import de.vinado.app.participate.notification.email.model.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.MimeType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class InMemoryAttachment implements Email.Attachment {

    @Getter
    private final String name;

    @Getter
    private final MimeType type;

    private final byte[] data;

    @Override
    public InputStream data() throws IOException {
        return new ByteArrayInputStream(data);
    }
}
