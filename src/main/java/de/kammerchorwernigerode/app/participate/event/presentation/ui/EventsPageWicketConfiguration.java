package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import de.kammerchorwernigerode.app.participate.event.presentation.ui.details.EventDetailsPage;
import de.kammerchorwernigerode.app.participate.wicket.configuration.WicketConfigurer;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventsPageWicketConfiguration implements WicketConfigurer {

    @Override
    public void init(ManagementWicketApplication webApplication) {
        mountPages(webApplication);
    }

    private void mountPages(WebApplication webApplication) {
        webApplication.mountPage("/events/${id}", EventDetailsPage.class);
    }
}
