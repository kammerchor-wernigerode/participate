package de.vinado.wicket.participate.components.panels;

import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.wicket.ApplicationName;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Footer extends Panel {

    @SpringBean
    private ApplicationName applicationName;

    @SpringBean
    private ApplicationProperties applicationProperties;

    public Footer(final String id) {
        super(id);

        final boolean developmentMode = applicationProperties.isDevelopmentMode();
        final boolean signedIn = ParticipateSession.get().isSignedIn();

        add(new Label("developmentMode", "Development Mode") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(developmentMode);
            }
        });
        add(new AjaxLink<Void>("feedback") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new SendFeedbackPanel(modal, new CompoundPropertyModel<>(new SendFeedbackDTO())));
                modal.show(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!developmentMode);
            }
        });
        add(new Label("customer", applicationProperties.getCustomer()));
        add(new Label("year", new SimpleDateFormat("yyyy").format(new Date())));
        add(new Label("applicationName", applicationName.get()));
        add(new Label("version", applicationProperties.getVersion()));
    }
}
