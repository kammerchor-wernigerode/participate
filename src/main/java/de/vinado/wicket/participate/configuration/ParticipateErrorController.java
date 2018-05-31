package de.vinado.wicket.participate.configuration;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Controller
public class ParticipateErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public void error(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        resp.sendRedirect("error/404");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
