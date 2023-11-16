package de.vinado.wicket.participate.ui.pages;

import org.apache.wicket.markup.html.link.AbstractLink;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PageNotFoundPage extends AbstractErrorPage {


    public PageNotFoundPage() {
    }

    @Override
    protected boolean showFooter() {
        return false;
    }

    @Override
    protected void addHomePageLink(AbstractLink homePageLink) {
        this.add(homePageLink);
    }

    @Override
    protected int getStatusCode() {
        return 404;
    }
}
