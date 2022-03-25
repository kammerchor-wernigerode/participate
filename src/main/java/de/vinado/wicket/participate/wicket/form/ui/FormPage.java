package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventPanel;
import de.vinado.wicket.participate.ui.form.EventDropDownForm;
import de.vinado.wicket.participate.ui.form.FormPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public class FormPage extends BasePage implements IGenericComponent<ParticipantDTO, FormPage> {

    private static final long serialVersionUID = -8963400167069618982L;

    @SpringBean
    private EventService eventService;

    public FormPage(PageParameters parameters) {
        super(parameters);

        Optional.of(parameters.get("token"))
            .map(StringValue::toOptionalString)
            .map(eventService::getParticipant)
            .map(ParticipantDTO::new)
            .map(CompoundPropertyModel::new)
            .ifPresent(this::setModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(navbar());
        add(form());
        add(event());
    }

    private Component navbar() {
        return new Navbar("navbar")
            .setBrandName(Model.of())
            .setPosition(Navbar.Position.TOP)
            .addComponents(new AbstractNavbarComponent(Navbar.ComponentPosition.RIGHT) {
                private static final long serialVersionUID = -3984063505376572594L;

                @Override
                public Component create(String markupId) {
                    return new EventDropDownForm(markupId, getModel().map(ParticipantDTO::getParticipant)) {
                        private static final long serialVersionUID = 7137415331187027778L;

                        @Override
                        protected void onEventChange(Participant participant) {
                            PageParameters pageParameters = new PageParameters(getPageParameters());
                            pageParameters.set("token", participant.getToken());
                            setResponsePage(new FormPage(pageParameters));
                        }
                    };
                }
            });
    }

    private Component form() {
        return new FormPanel("formPanel", getModel());
    }

    private Component event() {
        IModel<EventDetails> eventModel = getModel().map(ParticipantDTO::getEvent).map(eventService::getEventDetails);
        PersonContext personContext = () -> getModelObject().getSinger();
        IModel<ParticipantFilter> participantFilter = new CompoundPropertyModel<>(new ParticipantFilter());

        return new EventPanel("eventPanel", null, new CompoundPropertyModel<>(eventModel),
            false, personContext, participantFilter) {
            private static final long serialVersionUID = -7117624285946973330L;

            @Override
            protected void addQuickAccessAction(SerializableFunction<String, AbstractAction> constructor) {
            }

            @Override
            protected void addDropdownAction(SerializableFunction<String, AbstractAction> constructor) {
            }
        };
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        requireAuthentication();
    }

    private void requireAuthentication() {
        if (AuthenticatedWebSession.get().isSignedIn()) return;

        PageParameters parameters = new PageParameters(getPageParameters());
        throw new RestartResponseAtInterceptPageException(FormSignInPage.class, parameters);
    }
}
