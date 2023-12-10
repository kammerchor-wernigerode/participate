package de.vinado.wicket.participate.ui.administration.user;

import de.vinado.app.participate.wicket.form.FormComponentLabel;
import de.vinado.wicket.common.FocusBehavior;
import de.vinado.wicket.form.ConditionalValidator;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class AddUserPanel extends GenericPanel<AddUserDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    public AddUserPanel(String wicketId, IModel<AddUserDTO> model) {
        super(wicketId, model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        queue(form("form"));

        TextField<String> usernameTf = new TextField<>("username");
        usernameTf.setRequired(true);
        usernameTf.setLabel(new ResourceModel("username", "Username"));
        usernameTf.add(new FocusBehavior());
        usernameTf.add(new ConditionalValidator<>(this::ensureUnique,
            new ResourceModel("unique.user", "A user with this username already exists")));
        queue(usernameTf, new FormComponentLabel("usernameLabel", usernameTf));
    }

    private Form<AddUserDTO> form(String wicketId) {
        return new Form<>(wicketId, getModel()) {

            @Override
            protected void onSubmit() {
                super.onSubmit();

                AddUserPanel.this.onSubmit();
            }
        };
    }

    private boolean ensureUnique(String username) {
        return StringUtils.equalsIgnoreCase(username, getModelObject().getUsername()) || !userService.hasUser(username);
    }

    protected void onSubmit() {
        userService.createUser(getModelObject());
    }
}
