package de.vinado.app.participate.notification.email.model;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class Emails {

    public static final String DEFAULT_SUBJECT = "subject";

    public static Builder defaultEmail() {
        return builder()
            .subject(DEFAULT_SUBJECT)
            ;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String subject;
        private String textContent;
        private String htmlContent;
        private List<Email.Attachment> attachments = new ArrayList<>();

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder textContent(String textContent) {
            this.textContent = textContent;
            return this;
        }

        public Builder htmlContent(String htmlContent) {
            this.htmlContent = htmlContent;
            return this;
        }

        public Builder attachments(Collection<Email.Attachment> attachments) {
            this.attachments = new ArrayList<>(attachments);
            return this;
        }

        public Builder add(Email.Attachment attachment) {
            attachments.add(attachment);
            return this;
        }

        public Email build() {
            return new Email() {

                @Override
                public String subject() {
                    return subject;
                }

                @Override
                public Optional<String> textContent() {
                    return Optional.ofNullable(textContent);
                }

                @Override
                public Optional<String> htmlContent() {
                    return Optional.ofNullable(htmlContent);
                }

                @Override
                public Stream<Attachment> attachments() {
                    return attachments.stream();
                }
            };
        }
    }
}
