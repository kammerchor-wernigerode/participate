package de.vinado.wicket.common;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.link.AbstractLink;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AjaxFocusBehavior extends AbstractDefaultAjaxBehavior {

    @Override
    protected void onBind() {
        if (!(getComponent() instanceof FormComponent) && !(getComponent() instanceof AbstractLink)) {
            throw new IllegalArgumentException("AjaxFocusBehavior: Component must be instanceof FormComponent or instance of AbstractLink.");
        }
        super.onBind();
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        target.focusComponent(getComponent());
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(OnLoadHeaderItem.forScript(getCallbackScript().toString()));
    }
}
