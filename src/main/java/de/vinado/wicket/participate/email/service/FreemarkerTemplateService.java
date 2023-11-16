package de.vinado.wicket.participate.email.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import static java.util.Locale.getDefault;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class FreemarkerTemplateService implements TemplateService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final Configuration freeMarkerConfiguration;

    /**
     * Calls the template engine to process the given templateReference with the given model object.
     *
     * @param templateReference the templateReference file to be processed. The name must be compliant with the position
     *                          of the template your resources' folder. Usually, files in {@code
     *                          resources/templates} are resolved by passing the sole file name. Sub-folders of {@code
     *                          resources/templates} must be explicitly reported.
     * @param model             the model object to process the templateReference
     * @param type              the type of the template
     * @return the processed template
     *
     * @throws IOException       if the template reference file is not found or could not be accessed
     * @throws TemplateException if the template could not be processed
     */
    @Override
    public String processTemplate(String templateReference, Map<String, Object> model, MultipartType type)
        throws IOException, TemplateException {
        String templateFileName = StringUtils.trim(templateReference);

        if (StringUtils.isEmpty(templateFileName)) {
            throw new IllegalArgumentException("The template reference must not be null or empty");
        }

        templateFileName = templateReference.endsWith(templateExtensions(type))
            ? templateFileName
            : (templateFileName + "." + templateExtensions(type));

        Locale locale = getDefault();
        log.trace("Try to find template {} using locale {} and {} encoding", templateFileName, locale, UTF_8);
        Template template = freeMarkerConfiguration.getTemplate(templateFileName, locale, UTF_8);
        log.trace("Found.");

        String message = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        log.trace("Processed template w/ name={} and model", templateReference);

        return message;
    }

    @Override
    public String templateExtensions(MultipartType type) {
        switch (type) {
            case PLAIN:
            case HTML:
                return "ftl";
            default:
                throw new IllegalArgumentException("Multi part type is not supported");
        }
    }
}
