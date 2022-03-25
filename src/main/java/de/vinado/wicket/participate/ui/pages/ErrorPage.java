package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.heading.Heading;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class ErrorPage extends AbstractErrorPage {

    private boolean showStacktrace = false;

    private final int status;

    private Form form;

    private String message;

    private String stacktrace;

    public ErrorPage() {
        this(new Exception(), 500);
    }

    public ErrorPage(Exception e) {
        this(e, 500);
    }

    public ErrorPage(final Exception exception, int status) {
        super(new PageParameters());
        this.message = exception.getMessage();
        this.stacktrace = exception.toString();
        this.status = status;

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);
        stacktrace = writer.toString();

        form.add(new Heading("error", new ResourceModel("error", "Error")));

        form.add(new MultiLineLabel("message", new PropertyModel<>(this, "message")));

        final WebMarkupContainer stacktraceWmc = new WebMarkupContainer("stacktraceWmc") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(showStacktrace);
            }
        };
        stacktraceWmc.setOutputMarkupPlaceholderTag(true);
        form.add(stacktraceWmc);

        // Get values
        String resource = "";
        String markup = "";
        MarkupStream markupStream = null;

        if (exception instanceof MarkupException) {
            markupStream = ((MarkupException) exception).getMarkupStream();

            if (null != markupStream) {
                markup = markupStream.toHtmlDebugString();
                resource = markupStream.getResource().toString();
            }
        }

        // Create markup label
        final MultiLineLabel markupLabel = new MultiLineLabel("markup", markup);
        markupLabel.setEscapeModelStrings(false);

        // Add container with markup highlighted
        final MarkupStream finalMarkupStream = markupStream;
        final WebMarkupContainer markupWmc = new WebMarkupContainer("markupWmc") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(null != finalMarkupStream && showStacktrace);
            }
        };
        markupWmc.add(markupLabel);
        markupWmc.add(new Label("resource", resource));
        markupWmc.setOutputMarkupPlaceholderTag(true);
        markupWmc.setVisible(markupStream != null);
        form.add(markupWmc);

        final MultiLineLabel stacktraceLabel = new MultiLineLabel("stacktrace", new PropertyModel<>(this, "stacktrace"));
        stacktraceLabel.setOutputMarkupPlaceholderTag(true);
        stacktraceWmc.add(stacktraceLabel);

        form.add(new BootstrapAjaxButton("showStackTrace", new ResourceModel("show.stacktrace", "Show Stacktrace"), Buttons.Type.Primary) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                showStacktrace = !showStacktrace;
                target.add(stacktraceLabel);
                target.add(markupWmc);
                target.add(stacktraceWmc);
            }
        });
    }

    public String getMessage() {
        return message;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    protected void addHomePageLink(final AbstractLink homePageLink) {
        form = new Form("form");
        form.add(homePageLink);
        add(form);
    }

    @Override
    protected int getStatusCode() {
        return status;
    }
}
