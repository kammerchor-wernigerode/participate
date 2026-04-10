package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import de.kammerchorwernigerode.app.participate.wicket.WicketApplication;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Locale;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class BootstrapPage extends WebPage {

    public BootstrapPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        HtmlTag htmlTag = new HtmlTag("html");
        add(htmlTag);

        Label title = new Label("title", titleModel());
        htmlTag.add(title);

        Application application = Application.get();
        String filterName = application.getMetaData(WicketApplication.footerBucketNameKey);
        HeaderResponseContainer headerResponseContainer = new HeaderResponseContainer("footer-bucket", filterName);
        htmlTag.add(headerResponseContainer);
    }

    protected IModel<?> titleModel() {
        return Model.of();
    }

    public static boolean isMounted() {
        return RequestCycle.get().find(IPageRequestHandler.class)
            .map(handler -> handler.getPage())
            .map(BootstrapPage.class::isInstance)
            .orElse(false);
    }

    public static BootstrapPage get() {
        return RequestCycle.get().find(IPageRequestHandler.class)
            .map(handler -> handler.getPage())
            .filter(BootstrapPage.class::isInstance)
            .map(BootstrapPage.class::cast)
            .orElseThrow(() -> new WicketRuntimeException("BootstrapPage is not mounted"));
    }


    private static class HtmlTag extends TransparentWebMarkupContainer {

        public HtmlTag(String id) {
            super(id);
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);

            checkComponentTag(tag, "html");

            tag.put("lang", serialize(Session.get().getLocale()));
        }

        private String serialize(Locale locale) {
            return locale.toLanguageTag();
        }
    }
}
