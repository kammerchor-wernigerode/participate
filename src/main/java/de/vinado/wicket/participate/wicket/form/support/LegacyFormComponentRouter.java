package de.vinado.wicket.participate.wicket.form.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.vinado.wicket.participate.wicket.form.support.FormApplicationServletContextInitializer.APP_ROOT;

/**
 * @author Vincent Nadoll
 */
@Slf4j
@Component
@WebFilter(filterName = "legacy-form-router", urlPatterns = {"/participate"})
class LegacyFormComponentRouter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        String token = req.getParameter("token");
        if (Objects.equals("/participate", requestURI) && StringUtils.hasText(token)) {
            if (log.isDebugEnabled())
                log.debug("Redirecting legacy endpoint [" + requestURI + "] to new management component");
            res.sendRedirect(APP_ROOT + "/participant?token=" + token);
            return;
        }

        chain.doFilter(request, response);
    }
}
