package de.vinado.wicket.participate.ui.resetPassword;

import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ResetPasswordPage extends BasePage {

    public ResetPasswordPage() {
        this(new PageParameters());
    }

    public ResetPasswordPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final String tokenParameter = getPageParameters().get("token").to(String.class);

        add(new ResetPasswordPanel("resetPasswordPanel", tokenParameter));
        add(new BookmarkablePageLink("goHomeLink", getApplication().getHomePage()));
    }
}
