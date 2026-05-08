package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import de.kammerchorwernigerode.app.participate.wicket.WicketApplication;
import de.kammerchorwernigerode.app.participate.wicket.feedback.GlobalFeedbackMessageFilter;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.Modal;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.modal.ResetModalBehavior;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.toast.ToastContainer;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
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

import lombok.Getter;

public abstract class BootstrapPage extends WebPage {

    private final ToastContainer toaster;

    @Getter
    private final Modal modal;

    public BootstrapPage() {
        this(null);
    }

    public BootstrapPage(PageParameters parameters) {
        super(parameters);

        this.toaster = new ToastContainer("toaster", new GlobalFeedbackMessageFilter());
        this.modal = new Modal("modal");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        HtmlTag htmlTag = new HtmlTag("html");
        add(htmlTag);

        Label title = new Label("title", titleModel());
        htmlTag.add(title);

        toaster.setOutputMarkupId(true);
        htmlTag.add(toaster);

        modal.add(new ResetModalBehavior());
        htmlTag.add(modal);

        Application application = Application.get();
        String filterName = application.getMetaData(WicketApplication.footerBucketNameKey);
        HeaderResponseContainer headerResponseContainer = new HeaderResponseContainer("footer-bucket", filterName);
        htmlTag.add(headerResponseContainer);
    }

    protected IModel<?> titleModel() {
        return Model.of();
    }

    public void updateToaster() {
        RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> target.add(toaster));
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
