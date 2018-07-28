package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.wicket.participate.behavoirs.FocusBehavior;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.components.forms.validator.ConditionalValidator;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalPanel;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.data.dtos.AddUserDTO;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddUserPanel extends BootstrapModalPanel<AddUserDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    public AddUserPanel(final BootstrapModal modal, final IModel<AddUserDTO> model) {
        super(modal, new ResourceModel("user.add", "Add User"), model);

        final TextField<String> usernameTf = new TextField<>("username");
        usernameTf.add(BootstrapHorizontalFormDecorator.decorate());
        usernameTf.setRequired(true);
        usernameTf.add(new FocusBehavior());
        usernameTf.add(new ConditionalValidator<String>(new ResourceModel("unique.user", "A user with this username already exists")) {
            @Override
            public boolean getCondition(final String value) {
                if (Strings.isEmpty(model.getObject().getUsername())) {
                    return false;
                } else if (value.equals(model.getObject().getUsername())) {
                    return false;
                }

                return userService.hasUser(value);
            }
        });
        inner.add(usernameTf);
    }


    @Override
    protected void onSaveSubmit(final IModel<AddUserDTO> model, final AjaxRequestTarget target) {
        onConfirm(userService.createUser(model.getObject()), target);
    }

    protected abstract void onConfirm(final User user, final AjaxRequestTarget target);
}
