package de.vinado.wicket.participate.behavoirs.decorators;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
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
        final boolean invalid = !fc.isValid();
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;
        final boolean checkBox = fc instanceof CheckBox;

        ComponentTag formGroup = new ComponentTag("div", XmlTag.TagType.OPEN);
        formGroup.setId(getAjaxRegionMarkupId(component));
        formGroup.put("class", "col-12");
        formGroup.writeOutput(r, wicketAttributes, namespace);

        if (checkBox) {
            ComponentTag checkbox = new ComponentTag("div", XmlTag.TagType.OPEN);
            checkbox.put("class", "form-check");
            checkbox.writeOutput(r, wicketAttributes, namespace);
        } else {
            ComponentTag labelOpen = new ComponentTag("label", XmlTag.TagType.OPEN);
            labelOpen.put("class", "visually-hidden");
            labelOpen.put("for", fc.getMarkupId());
            labelOpen.writeOutput(r, wicketAttributes, namespace);

            String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
            String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();
            r.write(Strings.escapeMarkup(placeholder) + (required ? " *" : ""));

            new ComponentTag("label", XmlTag.TagType.CLOSE)
                .writeOutput(r, wicketAttributes, namespace);
        }
    }

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        final FormComponent<?> fc = (FormComponent<?>) component;

        final String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();

        final boolean invalid = !fc.isValid();
        final boolean checkBox = fc instanceof CheckBox;
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;

        tag.put("class", checkBox ? "form-check-input" : "form-control form-control-sm");
        tag.put("class", tag.getAttribute("class") + (invalid ? " is-invalid" : ""));

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
        final boolean invalid = !fc.isValid();
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;
        final boolean checkBox = fc instanceof CheckBox;

        ComponentTag formGroup = new ComponentTag("div", XmlTag.TagType.CLOSE);

        if (checkBox) {
            ComponentTag labelOpen = new ComponentTag("label", XmlTag.TagType.OPEN);
            labelOpen.put("class", "form-check-label");
            labelOpen.put("for", fc.getMarkupId());
            labelOpen.writeOutput(r, wicketAttributes, namespace);

            String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
            String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();
            r.write(" " + Strings.escapeMarkup(placeholder) + (required ? " *" : ""));

            new ComponentTag("label", XmlTag.TagType.CLOSE)
                .writeOutput(r, wicketAttributes, namespace);

            formGroup.writeOutput(r, wicketAttributes, namespace);
        }
        formGroup.writeOutput(r, wicketAttributes, namespace);
    }

    @Override
    protected String getMarkupSuffix() {
        return "bifd";
    }

    private boolean stripWicketTags() {
        return Application.get().getMarkupSettings().getStripWicketTags();
    }
}
