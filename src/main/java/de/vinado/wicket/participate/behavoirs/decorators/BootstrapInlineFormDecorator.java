package de.vinado.wicket.participate.behavoirs.decorators;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
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

        final boolean required = fc.isRequired();
        final boolean invalid = !fc.isValid();
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;
        final boolean checkBox = fc instanceof CheckBox;

        if (!checkBox && !radioGroup && !checkGroup) {
            r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" " +
                    "class=\"form-group form-group-sm" +
                    (invalid ? " has-error" : "") +
                    "\">\n");
        }

        if (checkBox) {
            if (invalid) {
                r.write("<div class=\"has-error\">\n");
            }
            r.write("<div class=\"checkbox\">\n");
            r.write("<label class=\"" + (invalid ? "has-error" : "") + (required ? " required" : "") + "\"" +
                    " for=\"" + fc.getMarkupId() + "\">\n");
        }
    }

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        final FormComponent<?> fc = (FormComponent<?>) component;

        final String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();

        final boolean checkBox = fc instanceof CheckBox;
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;

        if (!checkBox && !radioGroup && !checkGroup) {
            tag.put("class", "form-control");
            if (null != placeholder) {
                tag.put("placeholder", placeholder);
            }
        }
    }

    @Override
    public void afterRender(final Component component) {
        final FormComponent<?> fc = (FormComponent<?>) component;
        final Response r = component.getResponse();

        final String defaultPlaceholder = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String placeholder = null == placeholderModel ? defaultPlaceholder : placeholderModel.getObject();

        final boolean required = fc.isRequired();
        final boolean invalid = !fc.isValid();
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;
        final boolean checkBox = fc instanceof CheckBox;

        if (checkBox) {
            r.write(" " + Strings.escapeMarkup(placeholder));
            r.write((required ? " *" : "") + "</label>\n");
            r.write("</div>\n");
            if (invalid) {
                r.write("</div>\n");
            }
        }

        if (!checkBox && !radioGroup && !checkGroup) {
            r.write("</div>\n");
        }
    }

    @Override
    protected String getMarkupSuffix() {
        return "bifd";
    }
}
