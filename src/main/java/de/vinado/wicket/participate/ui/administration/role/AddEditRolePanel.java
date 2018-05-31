package de.vinado.wicket.participate.ui.administration.role;

import de.vinado.wicket.participate.component.behavoir.AutosizeBehavior;
import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.validator.ConditionalValidator;
import de.vinado.wicket.participate.data.dto.RoleDTO;
import de.vinado.wicket.participate.data.permission.Role;
import de.vinado.wicket.participate.service.RoleService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AddEditRolePanel extends BootstrapModalPanel<RoleDTO> {

    @SpringBean
    @SuppressWarnings("unused")
    private RoleService roleService;

    private boolean modifyState;

    public AddEditRolePanel(final BootstrapModal modal, final IModel<String> title, final IModel<RoleDTO> model) {
        super(modal, title, model);
        modifyState = null != model.getObject().getRole();

        final TextField<String> nameTf = new TextField<>("name");
        nameTf.setLabel(new ResourceModel("romeName", "Role name"));
        nameTf.add(BootstrapHorizontalFormDecorator.decorate());
        nameTf.add(new ConditionalValidator<String>(new ResourceModel("uniqueRoleE", "A role with this name already exists!")) {
            @Override
            public boolean getCondition(final String value) {
                return roleService.roleExist(value);
            }
        });
        inner.add(nameTf);

        final TextArea descriptionTa = new TextArea("description");
        descriptionTa.add(BootstrapHorizontalFormDecorator.decorate());
        descriptionTa.add(new AutosizeBehavior());
        inner.add(descriptionTa);
    }

    @Override
    protected void onSaveSubmit(final IModel<RoleDTO> model, final AjaxRequestTarget target) {
        if (modifyState) {
            onUpdate(roleService.saveRole(model.getObject()), target);
        } else {
            onUpdate(roleService.createRole(model.getObject()), target);
        }
    }

    protected abstract void onUpdate(final Role role, final AjaxRequestTarget target);
}
