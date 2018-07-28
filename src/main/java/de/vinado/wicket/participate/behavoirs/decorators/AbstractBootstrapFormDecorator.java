package de.vinado.wicket.participate.behavoirs.decorators;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.IAjaxRegionMarkupIdProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AbstractBootstrapFormDecorator extends BootstrapBaseBehavior implements IAjaxRegionMarkupIdProvider {

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        final FormComponent<?> fc = (FormComponent<?>) component;

        final boolean checkBox = fc instanceof CheckBox;
        final boolean radioGroup = fc instanceof RadioGroup;
        final boolean checkGroup = fc instanceof CheckGroup;

        if (!checkBox && !radioGroup && !checkGroup) {
            tag.put("class", "form-control");
        }
    }

    @Override
    public String getAjaxRegionMarkupId(final Component component) {
        return component.getMarkupId() + "_" + getMarkupSuffix();
    }

    protected abstract String getMarkupSuffix();
}
