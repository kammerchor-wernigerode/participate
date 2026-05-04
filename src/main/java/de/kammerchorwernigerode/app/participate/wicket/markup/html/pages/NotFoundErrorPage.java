package de.kammerchorwernigerode.app.participate.wicket.markup.html.pages;

import de.kammerchorwernigerode.app.participate.wicket.request.ErrorAttributes;
import org.apache.wicket.request.http.WebResponse;

import jakarta.servlet.http.HttpServletResponse;

public class NotFoundErrorPage extends ExceptionErrorPage {

    public NotFoundErrorPage() {
        super();
    }

    public NotFoundErrorPage(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}

