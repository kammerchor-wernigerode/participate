package de.vinado.wicket.bt4.form.decorator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.parser.XmlTag.TagType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

public class BootstrapHorizontalFormDecorator extends AbstractBootstrapFormDecorator {

    private IModel<String> labelModel;

    private BootstrapHorizontalFormDecorator(IModel<String> labelModel) {
        this.labelModel = labelModel;
    }

    public static BootstrapHorizontalFormDecorator decorate(IModel<String> labelModel) {
        return new BootstrapHorizontalFormDecorator(labelModel);
    }

    public static BootstrapHorizontalFormDecorator decorate() {
        return new BootstrapHorizontalFormDecorator(null);
    }

    public IModel<String> getLabelModel() {
        return labelModel;
    }

    @Override
    public void bind(Component component) {
        component.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void beforeRender(Component component) {
        FormComponent<?> fc = (FormComponent<?>) component;
        Response r = component.getResponse();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();
        boolean wicketAttributes = stripWicketTags();

        String defaultLabel = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        String label = null == labelModel ? defaultLabel : labelModel.getObject();

        boolean required = fc.isRequired();
        boolean checkBox = fc instanceof CheckBox;

        r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" class=\"form-group row\">");

        if (!checkBox) {
            ComponentTag labelTag = new ComponentTag("label", TagType.OPEN);
            labelTag.put("class", "col-sm-3 col-form-label");
            labelTag.put("for", fc.getMarkupId());
            labelTag.writeOutput(r, wicketAttributes, namespace);

            r.write(Strings.escapeMarkup(label) + (required ? " *" : ""));
            r.write(labelTag.syntheticCloseTagString());
        }

        ComponentTag inputContainer = new ComponentTag("div", TagType.OPEN);
        inputContainer.setId(getAjaxRegionMarkupId(component));
        inputContainer.put("class", "col-sm-9");
        inputContainer.put("class", inputContainer.getAttribute("class") + (checkBox ? " offset-sm-3" : ""));
        inputContainer.writeOutput(r, wicketAttributes, namespace);

        if (checkBox) {
            ComponentTag formCheck = new ComponentTag("div", TagType.OPEN);
            formCheck.put("class", "form-check");
            formCheck.writeOutput(r, wicketAttributes, namespace);
        }
    }

    @Override
    public void afterRender(Component component) {
        FormComponent<?> fc = (FormComponent<?>) component;
        Response r = component.getResponse();
        boolean wicketAttributes = stripWicketTags();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();

        String defaultLabel = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        String label = null == getLabelModel() ? defaultLabel : getLabelModel().getObject();

        boolean required = fc.isRequired();
        boolean checkBox = fc instanceof CheckBox;

        ComponentTag close = new ComponentTag("div", TagType.CLOSE);
        if (checkBox) {
            ComponentTag labelTag = new ComponentTag("label", TagType.OPEN);
            labelTag.put("class", "form-check-label");
            labelTag.put("for", fc.getMarkupId());
            labelTag.writeOutput(r, wicketAttributes, namespace);

            r.write(Strings.escapeMarkup(label) + (required ? " *" : ""));
            r.write(labelTag.syntheticCloseTagString());

            close.writeOutput(r, wicketAttributes, namespace);
        }

        close.writeOutput(r, wicketAttributes, namespace);
        close.writeOutput(r, wicketAttributes, namespace);
    }

    @Override
    protected String getMarkupSuffix() {
        return "bhfd";
    }
}
