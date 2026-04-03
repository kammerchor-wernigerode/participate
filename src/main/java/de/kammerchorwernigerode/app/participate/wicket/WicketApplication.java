package de.kammerchorwernigerode.app.participate.wicket;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.DebugSettings;
import org.apache.wicket.settings.DebugSettings.ClassOutputStrategy;
import org.apache.wicket.settings.MarkupSettings;

import java.nio.charset.StandardCharsets;

import lombok.Setter;

public abstract class WicketApplication extends WebApplication {

    @Setter
    private WebjarsSettings webjarsSettings;

    @Override
    protected void init() {
        super.init();

        MarkupSettings markupSettings = getMarkupSettings();
        configure(markupSettings);

        DebugSettings debugSettings = getDebugSettings();
        configure(debugSettings);

        WebjarsSettings webjarsSettings = getWebjarsSettings();
        configure(webjarsSettings);
    }

    protected void configure(MarkupSettings settings) {
        settings.setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        settings.setCompressWhitespace(usesDeploymentConfig());
        settings.setStripComments(usesDeploymentConfig());
        settings.setStripWicketTags(true);
    }

    protected void configure(DebugSettings settings) {
        if (usesDevelopmentConfig()) {
            settings.setComponentPathAttributeName("data-wicket-path");
            settings.setOutputMarkupContainerClassNameStrategy(ClassOutputStrategy.HTML_COMMENT);
        }
    }

    protected void configure(WebjarsSettings settings) {
        settings.useCdnResources(false);
    }

    public WebjarsSettings getWebjarsSettings() {
        if (null == webjarsSettings) {
            webjarsSettings = new WebjarsSettings();
        }
        return webjarsSettings;
    }
}
