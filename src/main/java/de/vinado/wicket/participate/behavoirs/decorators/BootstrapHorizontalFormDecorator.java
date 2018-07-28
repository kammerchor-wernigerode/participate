package de.vinado.wicket.participate.behavoirs.decorators;

import org.apache.wicket.Component;
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
public class BootstrapHorizontalFormDecorator extends AbstractBootstrapFormDecorator {

    private IModel<String> labelModel;

    private IModel<String> helperBlock;

    private BootstrapHorizontalFormDecorator(final IModel<String> labelModel, final IModel<String> helperBlock) {
        this.labelModel = labelModel;
        this.helperBlock = helperBlock;
    }

    public static BootstrapHorizontalFormDecorator decorate(final IModel<String> labelModel,
                                                            final IModel<String> helperText) {
        return new BootstrapHorizontalFormDecorator(labelModel, helperText);
    }

    public static BootstrapHorizontalFormDecorator decorate(final IModel<String> labelModel) {
        return new BootstrapHorizontalFormDecorator(labelModel, null);
    }

    public static BootstrapHorizontalFormDecorator decorateWithHelperBlock(final IModel<String> helperText) {
        return new BootstrapHorizontalFormDecorator(null, helperText);
    }

    public static BootstrapHorizontalFormDecorator decorate() {
        return new BootstrapHorizontalFormDecorator(null, null);
    }

    public IModel<String> getLabelModel() {
        return labelModel;
    }

    public IModel<String> getHelperBlock() {
        return helperBlock;
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
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;
        final boolean checkBox = fc instanceof CheckBox;

        r.write("<div id=\"" + getAjaxRegionMarkupId(component) + "\" " +
                "class=\"form-group form-group-sm" +
                (invalid ? " has-error" : "") +
                "\">\n");

        if (checkBox) {
            r.write("<div class=\"col-sm-offset-3 col-sm-9\">\n");
            r.write("<div class=\"checkbox\">\n");
            r.write("<label class=\"" + (invalid ? "has-error" : "") + (required ? " required" : "") + "\"" +
                    " for=\"" + fc.getMarkupId() + "\">\n");
        } else {
            r.write("<label for=\"" + component.getMarkupId() + "\" " +
                    "class=\"col-sm-3 control-label" + (required ? " required" : "") + (invalid ? " has-error" : "") + "\"" +
                    ">");
            r.write(Strings.escapeMarkup(label));
            r.write(required ? " *" : "  ");
            r.write("</label>\n");

            r.write("<div class=\"" + ((radioGroup || checkGroup) ? "col-sm-8" : "col-sm-9") + "\">\n");
        }
    }

    @Override
    public void afterRender(final Component component) {
        final FormComponent<?> fc = (FormComponent<?>) component;
        final Response r = component.getResponse();

        final String defaultLabel = null == fc.getLabel() ? fc.getDefaultLabel() : fc.getLabel().getObject();
        final String label = null == getLabelModel() ? defaultLabel : getLabelModel().getObject();

        final boolean required = fc.isRequired();
        final boolean checkBox = fc instanceof CheckBox;

        if (checkBox) {
            r.write(Strings.escapeMarkup(label));
            r.write((required ? " *" : "   ") + "</label>\n");
            r.write("</div>\n");
        }

        if (null != getHelperBlock()) {
            r.write("<p class=\"help-block\">");
            r.write(getHelperBlock().getObject());
            r.write("</p>\n");
        }

        r.write("</div>\n");
        r.write("</div>\n");
    }

    @Override
    protected String getMarkupSuffix() {
        return "bhfd";
    }
}
