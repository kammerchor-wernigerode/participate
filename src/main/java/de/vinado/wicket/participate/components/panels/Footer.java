package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.behavoirs.FooterBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Footer extends Panel {

    public Footer(final String id) {
        super(id);

        add(new FooterBehavior());

        final boolean developmentMode = ParticipateApplication.get().isInDevelopmentMode();

        add(new Label("developmentMode", "Development Mode") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(developmentMode);
            }
        });
        SendFeedbackPanel feedbackPanel = new SendFeedbackPanel("modal");
        add(new AjaxLink<Void>("feedback") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                feedbackPanel.show(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!developmentMode);
            }
        });
        add(new Label("customer", ParticipateApplication.get().getApplicationProperties().getCustomer()));
        add(new Label("year", new SimpleDateFormat("yyyy").format(new Date())));
        add(new Label("applicationName", ParticipateApplication.get().getApplicationName()));
        add(new Label("version", ParticipateApplication.get().getApplicationProperties().getVersion()));

        add(feedbackPanel);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("class", "footer");
    }
}
