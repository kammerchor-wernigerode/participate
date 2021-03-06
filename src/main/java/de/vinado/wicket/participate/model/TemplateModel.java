package de.vinado.wicket.participate.model;

import de.vinado.wicket.participate.email.service.MultipartType;
import de.vinado.wicket.participate.email.service.TemplateService;
import freemarker.template.TemplateException;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.IOException;
import java.util.Map;

/**
 * A model that represents a processed template string.
 *
 * @author Vincent Nadoll
 */
public class TemplateModel extends AbstractReadOnlyModel<String> {

    @SpringBean
    private TemplateService templateService;

    private final String templateName;
    private final IModel<String> defaultValue;
    private final Map<String, Object> data;

    /**
     * @param templateName the template to be processed
     * @param data         the model object to process the template
     */
    public TemplateModel(String templateName, Map<String, Object> data) {
        this(templateName, (IModel<String>) null, data);
    }

    /**
     * @param templateName the template to be processed
     * @param defaultValue value that will be returned if template doesn't exist
     * @param data         the model object to process the template
     */
    public TemplateModel(String templateName, String defaultValue, Map<String, Object> data) {
        this(templateName, Model.of(defaultValue), data);
    }

    /**
     * @param templateName the template to be processed
     * @param defaultValue value that will be returned if template doesn't exist
     * @param data         the model object to process the template
     */
    public TemplateModel(String templateName, IModel<String> defaultValue, Map<String, Object> data) {
        this.templateName = templateName;
        this.defaultValue = defaultValue;
        this.data = data;

        Injector.get().inject(this);
    }

    @Override
    public String getObject() {
        try {
            return templateService.processTemplate(templateName, data, MultipartType.PLAIN);
        } catch (IOException e) {
            return defaultValue.getObject();
        } catch (TemplateException e) {
            throw new WicketRuntimeException("Unable to process template");
        }
    }
}
