package de.vinado.app.participate.wicket.form;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

import static de.vinado.app.participate.wicket.form.FormConfiguration.APP_ROOT;

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
