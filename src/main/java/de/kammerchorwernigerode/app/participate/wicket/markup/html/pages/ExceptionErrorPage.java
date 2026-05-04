package de.kammerchorwernigerode.app.participate.wicket.markup.html.pages;

import de.kammerchorwernigerode.app.participate.wicket.request.ErrorAttributes;
import org.apache.wicket.Application;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.lang.Generics;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static jakarta.servlet.RequestDispatcher.ERROR_MESSAGE;
import static jakarta.servlet.RequestDispatcher.ERROR_REQUEST_URI;
import static jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE;

public class ExceptionErrorPage extends AbstractErrorPage
    implements IGenericComponent<ExceptionErrorPage.Data, ExceptionErrorPage> {

    public ExceptionErrorPage() {
        this(resolveErrorAttributes());
    }

    public ExceptionErrorPage(ErrorAttributes errorAttributes) {
        boolean showDetails = Application.get().usesDevelopmentConfig();
        Data data = new Data(errorAttributes, showDetails);
        if (showDetails && errorAttributes.getThrowable() instanceof MarkupException markupException) {
            MarkupStream markupStream = markupException.getMarkupStream();
            data.setResource(markupStream.getResource().toString());
            data.setMarkup(markupStream.toHtmlDebugString());
        }

        setModel(new CompoundPropertyModel<>(data));
    }

    private static ErrorAttributes resolveErrorAttributes() {
        RequestCycle requestCycle = RequestCycle.get();
        Request request = requestCycle.getRequest();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request.getContainerRequest();

        int statusCode = Optional.ofNullable(httpServletRequest.getAttribute(ERROR_STATUS_CODE))
            .map(ExceptionErrorPage::toInteger)
            .orElse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        String message = Optional.ofNullable(httpServletRequest.getAttribute(ERROR_MESSAGE))
            .map(Object::toString)
            .orElse(null);

        String path = Optional.ofNullable(httpServletRequest.getAttribute(ERROR_REQUEST_URI))
            .map(Object::toString)
            .orElse(null);

        Throwable throwable = Optional.ofNullable(httpServletRequest.getAttribute(ERROR_EXCEPTION))
            .filter(Throwable.class::isInstance)
            .map(Throwable.class::cast)
            .orElse(null);

        return new ErrorAttributes(statusCode, message, path, throwable);
    }

    private static Integer toInteger(Object object) {
        try {
            String string = object.toString();
            return Integer.valueOf(string);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<Data> model = getModel();

        WebMarkupContainer container = new WebMarkupContainer("container") {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);

                List<String> classes = new ArrayList<>();
                Data data = getModelObject();
                if (data.isShowDetails()) {
                    classes.add("container-fluid");
                } else {
                    classes.add("container");
                }
                classes.add("py-4");

                String cssClassNames = String.join(" ", classes);
                tag.put("class", cssClassNames);
            }
        };
        add(container);

        StringResourceModel titleModel = new StringResourceModel("ExceptionErrorPage.title.error", getModel());
        Label titleLabel = new Label("titleLabel", titleModel);
        container.add(titleLabel);

        MultiLineLabel messageLabel = new MultiLineLabel("message", model.map(this::getMessage)) {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                Data data = getModelObject();
                boolean showDetails = data.isShowDetails();
                setVisible(showDetails && !Strings.isEmpty(getMessage(data)));
            }
        };
        container.add(messageLabel);

        BookmarkablePageLink<Void> homepageLink = homePageLink("homepageLink");
        container.add(homepageLink);


        WebMarkupContainer markupContainer = new WebMarkupContainer("markupContainer") {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                Data data = model.getObject();
                boolean showDetails = data.isShowDetails();
                boolean showStacktrace = data.isShowStacktrace();
                String resource = data.getResource();
                setVisible(showDetails && showStacktrace && !Strings.isEmpty(resource));
            }
        };
        markupContainer.setOutputMarkupPlaceholderTag(true);
        container.add(markupContainer);

        WebMarkupContainer stacktraceContainer = new WebMarkupContainer("stacktraceContainer") {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                Data data = model.getObject();
                boolean showDetails = data.isShowDetails();
                boolean showStacktrace = data.isShowStacktrace();
                setVisible(showDetails && showStacktrace && null != getThrowable(data));
            }
        };
        stacktraceContainer.setOutputMarkupPlaceholderTag(true);
        container.add(stacktraceContainer);


        Label resourceLabel = new Label("resource", model.map(Data::getResource));
        markupContainer.add(resourceLabel);

        MultiLineLabel markupLabel = new MultiLineLabel("markup", model.map(Data::getMarkup));
        markupLabel.setEscapeModelStrings(false);
        markupContainer.add(markupLabel);


        IModel<String> stacktraceModel = model.map(this::getThrowable).map(this::printStacktrace);
        MultiLineLabel stacktraceLabel = new MultiLineLabel("stacktrace", stacktraceModel);
        stacktraceContainer.add(stacktraceLabel);


        AjaxLink<Void> toggleStacktraceLink = new AjaxLink<>("stacktraceToggle") {

            @Override
            protected void onConfigure() {
                super.onConfigure();

                Data data = model.getObject();
                boolean showDetails = data.isShowDetails();
                setVisible(showDetails && (null != getThrowable(data) || !Strings.isEmpty(data.getResource())));
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                Data data = model.getObject();
                data.toggleStacktrace();
                target.add(markupContainer);
                target.add(stacktraceContainer);
            }
        };
        container.add(toggleStacktraceLink);
    }

    private Throwable getThrowable(Data data) {
        ErrorAttributes attributes = data.getAttributes();
        return attributes.getThrowable();
    }

    private String printStacktrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        List<Throwable> al = convertToList(throwable);
        StringBuilder sb = new StringBuilder(256);

        int length = al.size() - 1;
        Throwable cause = al.get(length);

        sb.append("""
            Root cause:

            """);
        outputThrowable(cause, sb, false);

        if (length > 0) {
            sb.append("""


                Complete stack:

                """);
            for (int i = 0; i < length; i++) {
                outputThrowable(al.get(i), sb, true);
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private List<Throwable> convertToList(final Throwable throwable) {
        List<Throwable> al = Generics.newArrayList();
        Throwable cause = throwable;
        al.add(cause);
        while ((cause.getCause() != null) && (cause != cause.getCause())) {
            cause = cause.getCause();
            al.add(cause);
        }
        return al;
    }

    private void outputThrowable(Throwable cause, StringBuilder sb, boolean stopAtWicketServlet) {
        sb.append(cause);
        sb.append("\n");
        StackTraceElement[] trace = cause.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            String traceString = trace[i].toString();
            if (i > 1) {
                sb.append("     at ");
                sb.append(traceString);
                sb.append("\n");
                if (stopAtWicketServlet && traceString.startsWith("org.apache.wicket.protocol.http.WicketFilter")) {
                    return;
                }
            }
        }
    }

    private String getMessage(Data data) {
        ErrorAttributes attributes = data.getAttributes();
        return attributes.getMessage();
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);

        Data data = getModelObject();
        ErrorAttributes attributes = data.getAttributes();
        int statusCode = attributes.getStatusCode();
        response.setStatus(statusCode);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forCSS("pre p { margin: 0; }", null));
    }


    @lombok.Data
    static class Data implements Serializable {

        private final ErrorAttributes attributes;
        private final boolean showDetails;

        private String resource = "";
        private String markup = "";
        private boolean showStacktrace;

        public void toggleStacktrace() {
            showStacktrace = !showStacktrace;
        }
    }
}
