package de.kammerchorwernigerode.app.participate.person.presentation.ui;

import de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonsPage;
import de.kammerchorwernigerode.app.participate.wicket.configuration.WicketConfigurer;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
class PersonsWicketConfiguration implements WicketConfigurer {

    @Override
    public void init(ManagementWicketApplication webApplication) {
        mountPages(webApplication);
    }

    private void mountPages(WebApplication webApplication) {
        webApplication.mountPage("/persons", PersonsPage.class);
    }
}
