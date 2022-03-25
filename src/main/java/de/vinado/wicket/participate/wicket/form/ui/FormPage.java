package de.vinado.wicket.participate.wicket.form.ui;

import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Vincent Nadoll
 */
public class FormPage extends BasePage implements IGenericComponent<ParticipantDTO, FormPage> {

    private static final long serialVersionUID = -8963400167069618982L;

    public FormPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        requireAuthentication();
    }

    private void requireAuthentication() {
        if (AuthenticatedWebSession.get().isSignedIn()) return;

        PageParameters parameters = new PageParameters(getPageParameters());
        throw new RestartResponseAtInterceptPageException(SignInPage.class, parameters);
    }
}
