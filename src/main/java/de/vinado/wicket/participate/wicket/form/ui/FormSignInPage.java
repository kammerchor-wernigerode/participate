package de.vinado.wicket.participate.wicket.form.ui;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5CssReference;
import de.vinado.wicket.http.BadRequest;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Optional;

import static org.apache.wicket.markup.head.HtmlImportHeaderItem.forLinkTag;

/**
 * @author Vincent Nadoll
 */
public class FormSignInPage extends BasePage implements IGenericComponent<Participant, FormSignInPage> {

    @SpringBean
    private EventService eventService;

    private static final long serialVersionUID = 2300708725396474529L;

    public FormSignInPage(PageParameters parameters) {
        super(parameters);

        Optional.of(parameters.get("token"))
            .map(stringValue -> stringValue.to(String.class))
            .map(eventService::getParticipant)
            .map(participant -> (IModel<Participant>) () -> participant)
            .ifPresentOrElse(this::setModel, () -> {
                throw new MissingTokenException();
            });
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

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
        response.render(forLinkTag("shortcut icon", "favicon.ico", "image/x-icon"));
        response.render(CssHeaderItem.forReference(FontAwesome5CssReference.instance()));
    }


    private static final class MissingTokenException extends BadRequest {

        private static final long serialVersionUID = -3079760926120422921L;

        public MissingTokenException() {
            super("Required parameter 'token' is not present or malformed");
        }
    }
}
