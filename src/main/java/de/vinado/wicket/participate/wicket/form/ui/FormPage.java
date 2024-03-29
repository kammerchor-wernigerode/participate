package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.AbstractNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Optional;

public class FormPage extends BasePage implements IGenericComponent<ParticipantDTO, FormPage> {

    @SpringBean
    private EventService eventService;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Optional.of(getPageParameters().get("token"))
            .map(StringValue::toOptionalString)
            .map(eventService::getParticipant)
            .map(ParticipantDTO::new)
            .map(CompoundPropertyModel::new)
            .ifPresent(this::setModel);

        add(navbar());
        add(form());
        add(event());
    }

    private Component navbar() {
        return new Navbar("navbar")
            .setBrandName(Model.of())
            .setPosition(Navbar.Position.TOP)
            .addComponents(new AbstractNavbarComponent(Navbar.ComponentPosition.RIGHT) {

                @Override
                public Component create(String markupId) {
                    return new EventDropDownForm(markupId, LambdaModel.of(getModel(), ParticipantDTO::getParticipant, ParticipantDTO::setParticipant)) {

                        @Override
                        protected void onSelect(AjaxRequestTarget target) {
                            PageParameters pageParameters = new PageParameters(getPageParameters());
                            pageParameters.set("token", getModelObject().getToken());
                            setResponsePage(FormPage.class, pageParameters);
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

        return new EventPanel("eventPanel", new CompoundPropertyModel<>(eventModel),
            false, personContext, participantFilter) {

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
        if (AbstractAuthenticatedWebSession.get().isSignedIn()) return;

        PageParameters parameters = new PageParameters(getPageParameters());
        throw new RestartResponseAtInterceptPageException(FormSignInPage.class, parameters);
    }
}
