package de.vinado.app.participate.notification.email.model;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TemplatedEmailFactory {

    private final Configuration configuration;

    private final ApplicationProperties properties;

    public Email create(String subject, String plaintextTemplatePath, String htmlTemplatePath, Map<String, Object> data,
                        Locale locale) {
        return create(subject, plaintextTemplatePath, htmlTemplatePath, data, Collections.emptySet(), locale);
    }

    public Email create(String subject, String plaintextTemplatePath, String htmlTemplatePath, Map<String, Object> data,
                        Collection<Email.Attachment> attachments, Locale locale) {
        Assert.isTrue(plaintextTemplatePath != null || htmlTemplatePath != null, "At least one template path must be provided");
        populate(data);
        return new TemplatedEmail(subject, plaintextTemplatePath, htmlTemplatePath, data, configuration,
            new HashSet<>(attachments), locale);
    }

    private void populate(Map<String, Object> data) {
        data.put("baseUrl", properties.getBaseUrl());
        data.put("footer", properties.getMail().getFooter());
    }
}
