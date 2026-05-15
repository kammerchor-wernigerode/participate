package de.kammerchorwernigerode.app.participate.wicket.markup.html.pages;

import de.kammerchorwernigerode.app.participate.wicket.request.ErrorAttributes;
import org.apache.wicket.request.http.WebResponse;

import jakarta.servlet.http.HttpServletResponse;

public class ForbiddenErrorPage extends ExceptionErrorPage {

    public ForbiddenErrorPage() {
    }

    public ForbiddenErrorPage(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @Override
    protected void setHeaders(WebResponse response) {
        super.setHeaders(response);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
