package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.bt4.form.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.bt4.modal.FormModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.FocusBehavior;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddUserPanel extends FormModal<AddUserDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    public AddUserPanel(ModalAnchor anchor, IModel<AddUserDTO> model) {
        super(anchor, model);

        title(new ResourceModel("user.add", "Add User"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final TextField<String> usernameTf = new TextField<>("username");
        usernameTf.setRequired(true);
        usernameTf.add(new FocusBehavior());
        usernameTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.user", "A user with this username already exists")));
        form.add(usernameTf);

        addBootstrapHorizontalFormDecorator(form);
    }

    private boolean ensureUnique(String username) {
        return StringUtils.equalsIgnoreCase(username, getModelObject().getUsername()) || !userService.hasUser(username);
    }

    @Override
    protected void onSubmit(AjaxRequestTarget target) {
        onConfirm(userService.createUser(getModelObject()), target);
    }

    protected abstract void onConfirm(final User user, final AjaxRequestTarget target);
}
