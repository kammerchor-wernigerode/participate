package de.vinado.wicket.participate.email.service;

import freemarker.template.TemplateException;

import java.io.IOException;
import java.util.Map;

/**
 * Defines a service for processing templates with a template engine.
 *
 * @author Vincent Nadoll
 */
public interface TemplateService {

    /**
     * Calls the template engine to process the given templateReference with the given model object.
     *
     * @param templateReference the templateReference file to be processed. The name must be complaint with the position
     *                          of the template your your resources folder. Usually, files in {@code
     *                          resources/templates} are resolved by passing the sole file name. Subfolders of {@code
     *                          resources/templates} must be explicitly reported.
     * @param model             the model object to process the templateReference
     * @param type              the type of the template
     * @return the processed template
     *
     * @throws IOException       if the template reference file is not found or could not be accessed
     * @throws TemplateException if the template could not be processed
     */
    String processTemplate(String templateReference, Map<String, Object> model, MultipartType type)
        throws IOException, TemplateException;

    /**
     * @param type the type of the template
     * @return the expected extension of the template file
     */
    String templateExtensions(MultipartType type);
}
