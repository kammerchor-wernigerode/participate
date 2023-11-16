package de.vinado.app.participate.wicket.management;

import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static de.vinado.app.participate.wicket.management.ManagementConfiguration.APP_ROOT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
@WebFilter(filterName = "legacy-management-router",
    urlPatterns = {"/administration", "/error/*", "/events", "/event/*", "/login", "/resetPassword", "/singers"})
class LegacyManagementComponentRouter implements Filter {

    private static final Map<String, String> mappings;

    static {
        mappings = new HashMap<>();
        mappings.putAll(listRegisteredPaths().collect(toMap(identity(), path -> APP_ROOT + path)));
        mappings.put("/", APP_ROOT + "/events");
        mappings.put("/_", APP_ROOT + "/events");
        mappings.put("/_/", APP_ROOT + "/events");
    }

    private static Stream<String> listRegisteredPaths() {
        return ManagementPageRegistry.getInstance().getPaths();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String requestURI = req.getRequestURI();
        if (mappings.keySet().stream().anyMatch(source -> Objects.equals(source, requestURI))) {
            if (log.isDebugEnabled())
                log.debug("Redirecting legacy endpoint [" + requestURI + "] to new management component");
            res.sendRedirect(mappings.get(requestURI));
            return;
        }

        chain.doFilter(request, response);
    }
}
