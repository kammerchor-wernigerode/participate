package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import de.kammerchorwernigerode.app.participate.wicket.bootstrap.icon.BootstrapIconsResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BootstrapBehavior extends Behavior {

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(BootstrapCssResourceReference.asHeaderItem());
        response.render(BootstrapJavaScriptResourceReference.asHeaderItem());
        response.render(BootstrapIconsResourceReference.asHeaderItem());
    }

    public static BootstrapBehavior getInstance() {
        return Holder.INSTANCE;
    }


    private static class Holder {

        private static final BootstrapBehavior INSTANCE = new BootstrapBehavior();
    }
}
