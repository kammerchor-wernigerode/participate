package de.vinado.wicket.participate.ui.resetPassword;

import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class ResetPasswordPage extends BasePage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final String tokenParameter = getPageParameters().get("token").to(String.class);

        add(new ResetPasswordPanel("resetPasswordPanel", tokenParameter));
        add(new BookmarkablePageLink("goHomeLink", getApplication().getHomePage()));
    }
}
