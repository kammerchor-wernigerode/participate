package de.vinado.wicket.participate.model;

import de.vinado.app.participate.wicket.spring.Holder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class TemplateModel implements IModel<String> {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    @SpringBean
    private Holder<Configuration> configuration;

    private final String templateName;
    private final IModel<String> defaultValue;
    private final Map<String, Object> data;

    public TemplateModel(String templateName) {
        this(templateName, Collections.emptyMap());
    }

    public TemplateModel(String templateName, Map<String, Object> data) {
        this(templateName, (IModel<String>) null, data);
    }

    public TemplateModel(String templateName, String defaultValue, Map<String, Object> data) {
        this(templateName, Model.of(defaultValue), data);
    }

    public TemplateModel(String templateName, IModel<String> defaultValue, Map<String, Object> data) {
        this.templateName = templateName;
        this.defaultValue = defaultValue;
        this.data = data;

        Injector.get().inject(this);
    }

    @Override
    public String getObject() {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template(), data);
        } catch (IOException e) {
            return defaultValue.getObject();
        } catch (TemplateException e) {
            throw new WicketRuntimeException("Unable to process template");
        }
    }

    private Template template() throws IOException {
        Locale locale = Locale.getDefault();
        return configuration().getTemplate(templateName, locale, UTF_8);
    }

    private Configuration configuration() {
        return configuration.service();
    }


    @Component
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    @Getter
    private static class ConfigurationHolder implements Holder<Configuration> {

        private final Configuration service;
    }
}
