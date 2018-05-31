package de.vinado.wicket.participate.ui.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.heading.Heading;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateApplication;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ExpiredPage extends AbstractErrorPage {

    private int COUNTDOWN = 3;

    public ExpiredPage() {
    }

    public ExpiredPage(final PageParameters parameters) {
        super(parameters);

        final BootstrapBookmarkablePageLink<String> homePageLink = new BootstrapBookmarkablePageLink<>("homePageLink",
                getApplication().getHomePage(), Buttons.Type.Primary);
        homePageLink.setIconType(FontAwesomeIconType.home);
        homePageLink.setLabel(new ResourceModel("homepage", "Return to home page"));
        add(homePageLink);

        final Model<Integer> model = new Model<Integer>() {
            public Integer getObject() {
                return COUNTDOWN--;
            }
        };

        add(new Heading("heading", new ResourceModel("sessionExpired", "The session is expired")));

        final Label counterLabel = new Label("counter", model);
        counterLabel.setOutputMarkupId(true);
        counterLabel.add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(final AjaxRequestTarget target) {
                target.add(counterLabel);
                if (COUNTDOWN == 0) {
                    setResponsePage(ParticipateApplication.get().getHomePage());
                }
            }
        });
        add(counterLabel);
    }
}
