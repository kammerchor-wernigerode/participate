package de.vinado.wicket.participate.behavoirs.decorators;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.parser.XmlTag.TagType;
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
        boolean wicketAttributes = stripWicketTags();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();

        final boolean checkBox = fc instanceof CheckBox;

        ComponentTag formGroup = new ComponentTag("div", TagType.OPEN);
        formGroup.setId(getAjaxRegionMarkupId(component));
        formGroup.put("class", "form-group");
        formGroup.put("class", formGroup.getAttribute("class") + (checkBox ? " form-check" : ""));
        formGroup.writeOutput(r, wicketAttributes, namespace);

        if (!checkBox) insertLabel(component);
    }

    @Override
    public void afterRender(Component component) {
        Response response = component.getResponse();
        final boolean checkBox = component instanceof CheckBox;
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
