package de.vinado.wicket.participate.component.behavoir;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.AbstractLink;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FocusBehavior extends Behavior {

    @Override
    public void bind(final Component component) {
        if (!(component instanceof FormComponent) && !(component instanceof AbstractLink)) {
            throw new IllegalArgumentException("FocusBehavior: Component must be instanceof FormComponent or instanceof AbstractLink.");
        }
        component.setOutputMarkupId(true);
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        response.render(OnLoadHeaderItem.forScript("document.getElementById('" + component.getMarkupId() + "').focus();"));
    }
}
