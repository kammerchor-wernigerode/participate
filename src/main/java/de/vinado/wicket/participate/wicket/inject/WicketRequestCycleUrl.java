package de.vinado.wicket.participate.wicket.inject;

import lombok.SneakyThrows;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlRenderer;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * @author Vincent Nadoll
 */
@Component
public class WicketRequestCycleUrl implements RequestUrl {

    @SneakyThrows
    @Override
    public URL get() {
        UrlRenderer urlRenderer = new UrlRenderer(RequestCycle.get().getRequest());
        Url baseUrl = urlRenderer.getBaseUrl();
        String renderedUrl = urlRenderer.renderFullUrl(baseUrl);
        return new URL(renderedUrl);
    }
}
