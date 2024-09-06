package de.vinado.app.participate.notification.email.model;

import org.springframework.core.io.InputStreamSource;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

public interface Email {

    String subject();

    default Optional<String> textContent() {
        return Optional.empty();
    }

    default Optional<String> htmlContent() {
        return Optional.empty();
    }

    default Stream<Attachment> attachments() {
        return Stream.empty();
    }


    interface Attachment extends InputStreamSource {

        String name();

        MimeType type();

        InputStream data() throws IOException;

        @Override
        default InputStream getInputStream() throws IOException {
            return data();
        }
    }
}
