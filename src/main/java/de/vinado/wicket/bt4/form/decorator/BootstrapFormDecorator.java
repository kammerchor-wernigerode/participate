package de.vinado.wicket.bt4.form.decorator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.parser.XmlTag.TagType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

public class BootstrapFormDecorator extends AbstractBootstrapFormDecorator {

    private IModel<String> labelModel;

    public BootstrapFormDecorator(IModel<String> labelModel) {
        this.labelModel = labelModel;
    }

    public static BootstrapFormDecorator decorate(IModel<String> label) {
        return new BootstrapFormDecorator(label);
    }

    public static BootstrapFormDecorator decorate() {
        return new BootstrapFormDecorator(null);
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void beforeRender(Component component) {
        FormComponent<?> fc = (FormComponent<?>) component;
        Response r = component.getResponse();

        boolean checkBox = fc instanceof CheckBox;

        r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" class=\"form-group" + (checkBox ? " form-check" : "") + "\">");

        if (!checkBox) insertLabel(component);
    }

    @Override
    public void afterRender(Component component) {
        Response response = component.getResponse();
        boolean checkBox = component instanceof CheckBox;
        if (checkBox) insertLabel(component);
        response.write("</div>\n");
    }

    private void insertLabel(Component component) {
        FormComponent<?> fc = (FormComponent<?>) component;
        Response r = component.getResponse();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();

        String defaultLabel = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        String label = null == labelModel ? defaultLabel : labelModel.getObject();

        boolean wicketAttributes = stripWicketTags();
        boolean required = fc.isRequired();
        boolean checkBox = fc instanceof CheckBox;

        ComponentTag labelTag = new ComponentTag("label", TagType.OPEN);
        labelTag.put("class", checkBox ? "form-check-label" : "form-label");
        labelTag.put("for", fc.getMarkupId());
        labelTag.writeOutput(r, wicketAttributes, namespace);

        r.write(Strings.escapeMarkup(label) + (required ? " *" : ""));
        r.write(labelTag.syntheticCloseTagString());
    }

    @Override
    protected String getMarkupSuffix() {
        return "bfd";
    }
}
