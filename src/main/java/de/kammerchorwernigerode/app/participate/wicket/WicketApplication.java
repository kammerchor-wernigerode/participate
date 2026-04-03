package de.kammerchorwernigerode.app.participate.wicket;

import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapResourceAppender;
import org.apache.wicket.application.ComponentInitializationListenerCollection;
import org.apache.wicket.csp.CSPDirective;
import org.apache.wicket.csp.CSPDirectiveSrcValue;
import org.apache.wicket.csp.ContentSecurityPolicySettings;
import org.apache.wicket.markup.html.HeaderResponseDecoratorCollection;
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

        ContentSecurityPolicySettings cspSettings = getCspSettings();
        configure(cspSettings);

        DebugSettings debugSettings = getDebugSettings();
        configure(debugSettings);

        ComponentInitializationListenerCollection componentInitializationListeners =
            getComponentInitializationListeners();
        configure(componentInitializationListeners);

        WebjarsSettings webjarsSettings = getWebjarsSettings();
        configure(webjarsSettings);

        HeaderResponseDecoratorCollection headerResponseDecorators = getHeaderResponseDecorators();
        configure(headerResponseDecorators);
    }

    protected void configure(MarkupSettings settings) {
        settings.setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        settings.setCompressWhitespace(usesDeploymentConfig());
        settings.setStripComments(usesDeploymentConfig());
        settings.setStripWicketTags(true);
    }

    private void configure(ContentSecurityPolicySettings settings) {
        settings.blocking().strict()
            .add(CSPDirective.STYLE_SRC, CSPDirectiveSrcValue.SELF)
            .add(CSPDirective.IMG_SRC, "data:")
        ;
    }

    protected void configure(DebugSettings settings) {
        if (usesDevelopmentConfig()) {
            settings.setComponentPathAttributeName("data-wicket-path");
            settings.setOutputMarkupContainerClassNameStrategy(ClassOutputStrategy.HTML_COMMENT);
        }
    }

    private void configure(ComponentInitializationListenerCollection listeners) {
        listeners.add(new BootstrapResourceAppender());
    }

    protected void configure(WebjarsSettings settings) {
        settings.useCdnResources(false);
    }

    private void configure(HeaderResponseDecoratorCollection decorators) {
        decorators.add(new JavaScriptFilteredIntoFooterHeaderResponseDecorator());
    }

    public WebjarsSettings getWebjarsSettings() {
        if (null == webjarsSettings) {
            webjarsSettings = new WebjarsSettings();
        }
        return webjarsSettings;
    }
}
