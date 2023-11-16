package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5CssReference;
import de.vinado.wicket.http.BadRequest;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.ui.pages.Resources;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Optional;

public class FormSignInPage extends BasePage implements IGenericComponent<Participant, FormSignInPage> {

    @SpringBean
    private EventService eventService;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Optional.of(getPageParameters().get("token"))
            .map(stringValue -> stringValue.to(String.class))
            .map(eventService::getParticipant)
            .map(participant -> (IModel<Participant>) () -> participant)
            .ifPresentOrElse(this::setModel, () -> {
                throw new MissingTokenException();
            });

        setStatelessHint(true);

        remove("modal");

        add(new FormSignInPanel("signInPanel", getModel()));

        assertStatelessness();
    }

    private void assertStatelessness() {
        visitChildren((component, visit) -> {
            if (!component.isStateless()) throw new WicketRuntimeException("Child components must be stateless");
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no"));
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex, nofollow"));
        Resources.renderFavicons(response);
        response.render(CssHeaderItem.forReference(FontAwesome5CssReference.instance()));
    }


    private static final class MissingTokenException extends BadRequest {

        public MissingTokenException() {
            super("Required parameter 'token' is not present or malformed");
        }
    }
}
