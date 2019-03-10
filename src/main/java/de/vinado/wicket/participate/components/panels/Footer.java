package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.behavoirs.FooterBehavior;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

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
        final boolean signedIn = ParticipateSession.get().isSignedIn();

        add(new Label("developmentMode", "Development Mode") {
            @Override
            protected void onConfigure() {
                setVisible(developmentMode);
            }
        });
        add(new AjaxLink("feedback") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new SendFeedbackPanel(modal, new CompoundPropertyModel<>(new SendFeedbackDTO())));
                modal.show(target);
            }

            @Override
            protected void onConfigure() {
                setVisible(!developmentMode);
            }
        });
        add(new Label("customer", ParticipateApplication.get().getApplicationProperties().getCustomer()));
        add(new Label("year", new SimpleDateFormat("yyyy").format(new Date())));
        add(new Label("applicationName", ParticipateApplication.get().getApplicationName()));
        add(new Label("version", ParticipateApplication.get().getApplicationProperties().getVersion()));
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        tag.put("class", "footer");
    }
}
