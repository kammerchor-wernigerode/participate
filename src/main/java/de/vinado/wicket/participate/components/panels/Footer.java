package de.vinado.wicket.participate.components.panels;

import de.vinado.app.participate.wicket.WicketProperties;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.dtos.SendFeedbackDTO;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.wicket.inject.ApplicationName;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Footer extends Panel {

    @SpringBean
    private ApplicationName applicationName;

    @SpringBean
    private ApplicationProperties applicationProperties;

    @SpringBean
    private WicketProperties wicketProperties;

    public Footer(String id) {
        super(id);

        add(new Label("developmentMode", "Development Mode") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(isDevelopmentMode());
            }
        });
        add(new AjaxLink<Void>("feedback") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                modal.setContent(new SendFeedbackPanel(modal, new CompoundPropertyModel<>(new SendFeedbackDTO())));
                modal.show(target);
            }

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!isDevelopmentMode());
            }
        });
        add(new Label("customer", applicationProperties.getCustomer()));
        add(new Label("year", new SimpleDateFormat("yyyy").format(new Date())));
        add(new Label("applicationName", applicationName.get()));
        add(new Label("version", applicationProperties.getVersion()));
    }

    private boolean isDevelopmentMode() {
        return RuntimeConfigurationType.DEVELOPMENT.equals(wicketProperties.getRuntimeConfiguration());
    }
}
