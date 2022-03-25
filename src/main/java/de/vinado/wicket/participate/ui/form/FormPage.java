package de.vinado.wicket.participate.ui.form;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.vinado.wicket.participate.components.panels.Footer;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.wicket.form.ui.EventDropDownForm;
import de.vinado.wicket.participate.wicket.form.ui.FormPanel;
import org.apache.wicket.Component;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableFunction;

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
        if (null == model.getObject() && eventService.hasToken(token)) {
            model.setObject(eventService.getParticipant(token));
            this.model = model;
            setDefaultModel(this.model);
        } else {
            onConfigure();
        }

        final Navbar navbar = new Navbar("navbar");
        navbar.setOutputMarkupId(true);
        navbar.setPosition(Navbar.Position.TOP);
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

        IModel<ParticipantFilter> participantFilter = new CompoundPropertyModel<>(new ParticipantFilter());
        final EventPanel eventPanel = new EventPanel("eventPanel", null,
            new CompoundPropertyModel<>(eventService.getEventDetails(model.getObject().getEvent())), false, () -> model.getObject().getSinger(), participantFilter) {
            @Override
            protected void addQuickAccessAction(SerializableFunction<String, AbstractAction> constructor) {
            }

            @Override
            protected void addDropdownAction(SerializableFunction<String, AbstractAction> constructor) {
            }
        };
        add(eventPanel);

        final FormPanel formPanel = new FormPanel("formPanel",
            new CompoundPropertyModel<>(null == FormPage.this.model.getObject() ?
                new ParticipantDTO() : new ParticipantDTO(FormPage.this.model.getObject())));
        add(formPanel);

        add(new Footer("footer"));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        if (!signedIn) {
            setResponsePage(new FormSignInPage(new CompoundPropertyModel<>(null == FormPage.this.model.getObject() ?
                new ParticipantDTO() : new ParticipantDTO(FormPage.this.model.getObject()))));
        }
    }
}
