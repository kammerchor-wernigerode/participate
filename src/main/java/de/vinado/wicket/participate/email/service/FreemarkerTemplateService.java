package de.vinado.wicket.participate.email.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.getDefault;

/**
 * Service implementation for the FreeMarker template engine.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Primary
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FreemarkerTemplateService implements TemplateService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final Configuration freeMarkerConfiguration;

    /**
     * {@inheritDoc}
     *
     * @param templateReference the templateReference file to be processed. The name must be complaint with the position
     *                          of the template your your resources folder. Usually, files in {@code
     *                          resources/templates} are resolved by passing the sole file name. Subfolders of {@code
     *                          resources/templates} must be explicitly reported.
     * @param model             the model object to process the templateReference
     * @param type              the type of the template
     * @return
     *
     * @throws IOException
     * @throws TemplateException
     */
    @Override
    public String processTemplate(final String templateReference, final Map<String, Object> model, final MultipartType type)
        throws IOException, TemplateException {
        String templateFileName = StringUtils.trim(templateReference);

        if (StringUtils.isEmpty(templateFileName)) {
            throw new IllegalArgumentException("The template reference must not be null or empty");
        }

        templateFileName = templateReference.endsWith(templateExtensions(type))
            ? templateFileName
            : (templateFileName + "." + templateExtensions(type));

        final Locale locale = getDefault();
        log.trace("Try to find template {} using locale {} and {} encoding", templateFileName, locale, UTF_8);
        final Template template = freeMarkerConfiguration.getTemplate(templateFileName, locale, UTF_8);
        log.trace("Found.");

        final String message = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        log.trace("Processed template w/ name={} and model", templateReference);

        return message;
    }

    /**
     * {@inheritDoc}
     *
     * @param type the type of the template
     * @return
     */
    @Override
    public String templateExtensions(final MultipartType type) {
        switch (type) {
            case PLAIN:
            case HTML:
                return "ftl";
            default:
                throw new IllegalArgumentException("Multi part type is not supported");
        }
    }
}
