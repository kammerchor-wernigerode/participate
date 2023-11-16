package de.vinado.wicket.participate.email.service;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

public interface TemplateService {

    String processTemplate(String templateReference, Map<String, Object> model, MultipartType type)
        throws IOException, TemplateException;

    String templateExtensions(MultipartType type);
}
