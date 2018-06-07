package de.vinado.wicket.participate.ui.administration.role;

import de.vinado.wicket.participate.component.behavoir.decorator.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.dto.EditRolePermissionDTO;
import de.vinado.wicket.participate.data.permission.RoleToPermission;
import de.vinado.wicket.participate.service.RoleService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EditPermissionPanel<T extends EditRolePermissionDTO> extends BootstrapModalPanel<T> {

    @SpringBean
    @SuppressWarnings("unused")
    private RoleService roleService;

    public EditPermissionPanel(final BootstrapModal modal, final IModel<T> model) {
        super(modal, new ResourceModel("role.edit", "Edit Role"), model);

        model.getObject().setPermissions(roleService.getRoleToPermission4Role(model.getObject().getRole()));

        final ListMultipleChoice<RoleToPermission> permissions = new ListMultipleChoice<>(
                "permissions",
                new LoadableDetachableModel<List<? extends RoleToPermission>>() {
                    @Override
                    protected List<? extends RoleToPermission> load() {
                        return roleService.getRoleToPermission4Role(roleService.getAdministratorRole());
                    }
                }, new ChoiceRenderer<>("permission.name"));
        permissions.setRequired(true);
        permissions.add(BootstrapHorizontalFormDecorator.decorate());
        inner.add(permissions);
    }

    @Override
    protected void onSaveSubmit(final IModel<T> model, final AjaxRequestTarget target) {
        roleService.savePermissions(model.getObject());
    }
}
