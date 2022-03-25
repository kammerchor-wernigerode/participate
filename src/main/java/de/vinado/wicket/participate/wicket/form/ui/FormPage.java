package de.vinado.wicket.participate.wicket.form.ui;

import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public class FormPage extends BasePage implements IGenericComponent<ParticipantDTO, FormPage> {

    private static final long serialVersionUID = -8963400167069618982L;

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
