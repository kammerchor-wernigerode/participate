package de.kammerchorwernigerode.app.participate.wicket.request;

import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.pages.ExceptionErrorPage;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;

import jakarta.servlet.http.HttpServletResponse;

public class ExceptionMapper extends DefaultExceptionMapper implements IExceptionMapper {

    @Override
    public IRequestHandler map(Exception e) {
        RequestCycle requestCycle = RequestCycle.get();

        if (requestCycle.find(AjaxRequestTarget.class).isPresent()) {
            return super.map(e);
        }

        Request request = requestCycle.getRequest();
        Url requestUrl = request.getUrl();
        int statusCode = determineStatusCode(e);
        String errorMessage = e.getLocalizedMessage();
        ErrorAttributes errorAttributes = new ErrorAttributes(statusCode, errorMessage, requestUrl.toString(), e);
        ExceptionErrorPage page = new ExceptionErrorPage(errorAttributes);

        PageProvider pageProvider = new PageProvider(page);
        return new RenderPageRequestHandler(pageProvider);
    }

    private int determineStatusCode(Throwable throwable) {
        Throwable cause = throwable;
        while (null != cause && cause.getCause() != cause) {
            if (cause instanceof ModelNotFoundException) {
                return HttpServletResponse.SC_NOT_FOUND;
            }

            cause = cause.getCause();
        }

        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
