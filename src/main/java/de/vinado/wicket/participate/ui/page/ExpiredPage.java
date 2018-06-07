package de.vinado.wicket.participate.ui.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.heading.Heading;
import de.vinado.wicket.participate.ParticipateApplication;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
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

        final Model<Integer> model = new Model<Integer>() {
            public Integer getObject() {
                return COUNTDOWN--;
            }
        };

        add(new Heading("heading", new ResourceModel("page.error.expired.session", "The session is expired")));

        final Label message = new Label("message", new StringResourceModel("page.error.expired.message").setParameters(model.getObject()));
        message.setOutputMarkupId(true);
        message.add(new AbstractAjaxTimerBehavior(Duration.seconds(1)) {
            @Override
            protected void onTimer(final AjaxRequestTarget target) {
                target.add(message);
                if (COUNTDOWN == 0) {
                    setResponsePage(ParticipateApplication.get().getHomePage());
                }
            }
        });
        add(message);
    }

    @Override
    protected void addHomePageLink(final AbstractLink homePageLink) {
        this.add(homePageLink);
    }

    @Override
    protected int getStatusCode() {
        return 500;
    }
}
