package de.vinado.wicket.participate.behavoirs.decorators;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapFormDecorator extends AbstractBootstrapFormDecorator {

    private IModel<String> labelModel;

    public BootstrapFormDecorator(final IModel<String> labelModel) {
        this.labelModel = labelModel;
    }

    public static BootstrapFormDecorator decorate(final IModel<String> label) {
        return new BootstrapFormDecorator(label);
    }

    public static BootstrapFormDecorator decorate() {
        return new BootstrapFormDecorator(null);
    }

    @Override
    public void bind(final Component component) {
        component.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void beforeRender(final Component component) {
        final FormComponent<?> fc = (FormComponent<?>) component;
        final Response r = component.getResponse();

        final String defaultLabel = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String label = null == labelModel ? defaultLabel : labelModel.getObject();

        final boolean required = fc.isRequired();
        final boolean invalid = !fc.isValid();

        r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" " +
                "class=\"form-group form-group-sm" +
                (invalid ? " has-error" : "") +
                "\">\n");
        r.write("<label for=\"" + component.getMarkupId() + "\" " +
                "class=\"" + (required ? " required" : "") + (invalid ? " has-error" : "") + "\"" +
                ">");
        r.write(Strings.escapeMarkup(label));
        r.write(required ? "*" : "");
        r.write("</label>\n");
    }

    @Override
    public void afterRender(final Component component) {
        component.getResponse().write("</div>\n");
    }

    @Override
    protected String getMarkupSuffix() {
        return "bfd";
    }
}
