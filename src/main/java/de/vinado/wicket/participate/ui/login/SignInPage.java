package de.vinado.wicket.participate.ui.login;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5CssReference;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.ui.pages.Resources;
import de.vinado.wicket.participate.wicket.inject.ApplicationName;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll
 */
public class SignInPage extends BasePage {

    @SpringBean
    private ApplicationName applicationName;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setStatelessHint(true);
        forceStateless();
        remove("modal");

        add(customerLabel("customer"));
        add(signInPanel("signInPanel"));

        add(passwordResetFrom("passwordResetForm"));
    }

    private void forceStateless() {
        visitChildren((component, visit) -> {
            if (!component.isStateless()) throw new WicketRuntimeException("Child components must be stateless");
        });
    }

    private Component customerLabel(String id) {
        return new Label(id, applicationName.get());
    }

    private Component signInPanel(String id) {
        return new SignInPanel(id);
    }

    private Form<?> passwordResetFrom(String id) {
        return new PasswordResetForm(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no"));
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex, nofollow"));
        Resources.renderFavicons(response);
        response.render(CssHeaderItem.forReference(FontAwesome5CssReference.instance()));
    }
}
