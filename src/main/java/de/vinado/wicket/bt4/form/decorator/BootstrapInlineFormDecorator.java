package de.vinado.wicket.bt4.form.decorator;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.util.string.Strings;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapInlineFormDecorator extends AbstractBootstrapFormDecorator {

    private IModel<String> placeholderModel;

    public BootstrapInlineFormDecorator(final IModel<String> placeholderModel) {
        this.placeholderModel = placeholderModel;
    }

    public static BootstrapInlineFormDecorator decorate(final IModel<String> placeholder) {
        return new BootstrapInlineFormDecorator(placeholder);
    }

    public static BootstrapInlineFormDecorator decorate() {
        return new BootstrapInlineFormDecorator(null);
    }

    public IModel<String> getPlaceholderModel() {
        return placeholderModel;
    }

    @Override
    public void bind(final Component component) {
        component.setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public void beforeRender(final Component component) {
        final FormComponent<?> fc = (FormComponent<?>) component;
        final Response r = component.getResponse();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();
        boolean wicketAttributes = stripWicketTags();

        final boolean required = fc.isRequired();
        final boolean checkBox = fc instanceof CheckBox;

        if (checkBox) {
            r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" class=\"form-check mb-2 mr-sm-2\">");
        } else {
            r.write("<span id=\"" + getAjaxRegionMarkupId(component) + "\">");

            ComponentTag labelTag = new ComponentTag("label", XmlTag.TagType.OPEN);
            labelTag.put("class", "sr-only");
            labelTag.put("for", fc.getMarkupId());
            labelTag.writeOutput(r, wicketAttributes, namespace);

            String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
            String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();
            r.write(Strings.escapeMarkup(placeholder) + (required ? " *" : ""));
            r.write(labelTag.syntheticCloseTagString());
        }
    }

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        super.onComponentTag(component, tag);

        final FormComponent<?> fc = (FormComponent<?>) component;
        final String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();
        boolean checkbox = fc instanceof CheckBox;

        if (!checkbox) {
            tag.append("class", "mb-2 mr-sm-2", " ");
        }

        if (null != placeholder) {
            tag.put("placeholder", placeholder);
        }
    }

    @Override
    public void afterRender(final Component component) {
        final FormComponent<?> fc = (FormComponent<?>) component;
        final Response r = component.getResponse();
        String namespace = fc.getMarkup().getMarkupResourceStream().getWicketNamespace();
        boolean wicketAttributes = stripWicketTags();

        final boolean required = fc.isRequired();
        final boolean checkBox = fc instanceof CheckBox;

        ComponentTag formGroup = new ComponentTag("div", XmlTag.TagType.CLOSE);

        if (checkBox) {
            ComponentTag labelTag = new ComponentTag("label", XmlTag.TagType.OPEN);
            labelTag.put("class", "form-check-label");
            labelTag.put("for", fc.getMarkupId());
            labelTag.writeOutput(r, wicketAttributes, namespace);

            String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
            String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();
            r.write(" " + Strings.escapeMarkup(placeholder) + (required ? " *" : ""));
            r.write(labelTag.syntheticCloseTagString());

            formGroup.writeOutput(r, wicketAttributes, namespace);
        } else {
            r.write("</span>");
        }
    }

    @Override
    protected String getMarkupSuffix() {
        return "bifd";
    }
}
