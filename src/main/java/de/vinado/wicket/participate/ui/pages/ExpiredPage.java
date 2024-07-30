package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.heading.Heading;
import org.apache.wicket.Application;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.time.Duration;

public class ExpiredPage extends AbstractErrorPage {

    private int COUNTDOWN = 3;

    public ExpiredPage() {
    }

    public ExpiredPage(PageParameters parameters) {
        super(parameters);

        Model<Integer> model = new Model<Integer>() {
            public Integer getObject() {
                return COUNTDOWN--;
            }
        };

        add(new Heading("heading", new ResourceModel("page.error.expired.session", "The session is expired")));

        Label message = new Label("message", new StringResourceModel("page.error.expired.message").setParameters(model.getObject()));
        message.setOutputMarkupId(true);
        message.add(new AbstractAjaxTimerBehavior(Duration.ofSeconds(1)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                target.add(message);
                if (COUNTDOWN == 0) {
                    setResponsePage(Application.get().getHomePage());
                }
            }
        });
        add(message);
    }

    @Override
    protected void addHomePageLink(AbstractLink homePageLink) {
        this.add(homePageLink);
    }

    @Override
    protected int getStatusCode() {
        return 500;
    }
}
