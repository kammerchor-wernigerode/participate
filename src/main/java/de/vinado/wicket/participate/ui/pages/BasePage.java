package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.vinado.wicket.participate.resources.css.SnackbarCssResourceReference;
import de.vinado.wicket.participate.resources.js.SnackbarJsResourceReference;
import de.vinado.wicket.participate.wicket.inject.ApplicationName;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class BasePage extends WebPage {

    @SpringBean
    private ApplicationName applicationName;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(html("html"));

        add(new HeaderResponseContainer("footer-container", "footer-container"));
    }

    private Component html(String id) {
        HtmlTag tag = new HtmlTag(id, getLocale());
        tag.add(new CssClassNameAppender("h-100"));
        return tag;
    }

    @Override
    protected void onBeforeRender() {
        addOrReplace(new Label("title", getTitle()));
        super.onBeforeRender();
    }

    protected IModel<String> getTitle() {
        return applicationName::get;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        Resources.render(response, this);
        response.render(SnackbarCssResourceReference.asHeaderItem());
        response.render(SnackbarJsResourceReference.asHeaderItem());
        response.render(JavaScriptHeaderItem.forScript("$(document).on('mouseup touchend', function (e) {\n" +
            "  var container = $('.bootstrap-datetimepicker-widget');\n" +
            "  if (!container.is(e.target) && container.has(e.target).length === 0) {\n" +
            "    container.parent().datetimepicker('hide');\n" +
            "  }\n" +
            "});", "datetimepicker_autohide"));
    }
}
