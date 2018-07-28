package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.components.panels.Footer;
import de.vinado.wicket.participate.data.EventDetails;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.dto.ParticipantDTO;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.ui.event.EventPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FormPage extends BasePage {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private IModel<Participant> model;

    private boolean signedIn;

    public FormPage() {
        this(new PageParameters());
    }

    public FormPage(final IModel<Participant> model, final boolean signedIn) {
        this(new PageParameters(), model, signedIn);
    }

    public FormPage(final PageParameters parameters) {
        this(parameters, Model.of(), false);
    }

    public FormPage(final PageParameters parameters, final IModel<Participant> model, final boolean signedIn) {
        super(parameters);
        this.signedIn = signedIn;
        this.model = model;

        setOutputMarkupId(true);

        final String token = parameters.get("token").to(String.class);
        if (null == model.getObject() && eventService.hasEventToken(token)) {
            model.setObject(eventService.getParticipant(token));
            this.model = model;
            setDefaultModel(this.model);
        } else {
            onConfigure();
        }

        final Navbar navbar = new Navbar("navbar");
        navbar.setOutputMarkupId(true);
        navbar.setPosition(Navbar.Position.STATIC_TOP);
        navbar.fluid();
        navbar.setBrandName(Model.of(""));
        navbar.addComponents(new AbstractNavbarComponent(Navbar.ComponentPosition.RIGHT) {
            @Override
            public Component create(final String markupId) {
                return new EventDropDownForm(markupId, FormPage.this.model) {
                    @Override
                    protected void onEventChange(final Participant participant) {
                        setResponsePage(new FormPage(new CompoundPropertyModel<>(participant), signedIn));
                    }
                };
            }
        });
        add(navbar);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final EventPanel eventPanel = new EventPanel("eventPanel", breadcrumb,
            new LoadableDetachableModel<EventDetails>() {
                @Override
                protected EventDetails load() {
                    if (null == model.getObject()) {
                        return eventService.getLatestEventView();
                    } else {
                        return eventService.getEventDetails(FormPage.this.model.getObject().getEvent());
                    }
                }
            }, false);
        add(eventPanel);

        final FormPanel formPanel = new FormPanel("formPanel", breadcrumb,
            new CompoundPropertyModel<>(null == FormPage.this.model.getObject() ?
                new ParticipantDTO() : new ParticipantDTO(FormPage.this.model.getObject())));
        add(formPanel);

        add(new Footer("footer"));
    }

    @Override
    protected void onConfigure() {
        if (!signedIn) {
            setResponsePage(new FormSignInPage(new CompoundPropertyModel<>(null == FormPage.this.model.getObject() ?
                new ParticipantDTO() : new ParticipantDTO(FormPage.this.model.getObject()))));
        }
    }
}
