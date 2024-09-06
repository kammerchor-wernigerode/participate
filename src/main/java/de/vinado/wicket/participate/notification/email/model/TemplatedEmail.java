package de.vinado.wicket.participate.notification.email.model;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
@ToString
public class TemplatedEmail implements Email {

    private static final String ENCODING = StandardCharsets.UTF_8.name();

    @Getter
    private final String subject;
    private final String plaintextTemplatePath;
    private final String htmlTemplatePath;
    private final Map<String, ?> data;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Configuration configuration;
    private final Set<Attachment> attachments;

    @Override
    public Optional<String> textContent() {
        return Optional.ofNullable(plaintextTemplatePath)
            .flatMap(this::content);
    }

    @Override
    public Optional<String> htmlContent() {
        return Optional.ofNullable(htmlTemplatePath)
            .flatMap(this::content);
    }

    public Stream<Attachment> attachments() {
        return attachments.stream();
    }

    private Optional<String> content(String templatePath) {
        try {
            String htmlContent = render(templatePath);
            return Optional.of(htmlContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String render(String templatePath) throws IOException, TemplateException {
        Writer buffer = new StringWriter();
        template(templatePath).process(data, buffer);
        return buffer.toString();
    }

    private Template template(String templatePath) throws IOException {
        Locale locale = Locale.getDefault();
        return configuration.getTemplate(templatePath, locale, ENCODING);
    }
}
