package de.vinado.wicket.participate.ui.administration.role;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalConfirmationPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.dto.EditRolePermissionDTO;
import de.vinado.wicket.participate.data.dto.PersonRoleDTO;
import de.vinado.wicket.participate.data.dto.RoleDTO;
import de.vinado.wicket.participate.data.permission.Role;
import de.vinado.wicket.participate.service.RoleService;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class RolePanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private RoleService roleService;

    public RolePanel(final String id, final IModel<Role> model, final boolean editable) {
        super(id, model);

        final Form form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        final WebMarkupContainer btnGroup = new WebMarkupContainer("btnGroup") {
            @Override
            protected void onConfigure() {
                setVisible(editable);
            }
        };
        btnGroup.setOutputMarkupId(true);
        wmc.add(btnGroup);

        final BootstrapAjaxLink defaultEditRoleBtn = new BootstrapAjaxLink("addPersonToRoleBtn", Buttons.Type.Default) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new AddRemovePersonToRolePanel(modal,
                        new CompoundPropertyModel<>(new PersonRoleDTO(model.getObject(), roleService.getPersons4Role(model.getObject())))));
                modal.show(target);
            }
        };
        defaultEditRoleBtn.setIconType(FontAwesomeIconType.plus_circle);
        defaultEditRoleBtn.setSize(Buttons.Size.Mini);
        defaultEditRoleBtn.setLabel(new ResourceModel("persons", "Persons"));
        btnGroup.add(defaultEditRoleBtn);

        final AjaxLink editPermissionsBtn = new AjaxLink("editPermissionsBtn") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                onPermissionsBtnClick(model, target);
            }
        };
        btnGroup.add(editPermissionsBtn);

        final AjaxLink editRoleBtn = new AjaxLink("editRoleBtn") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new AddEditRolePanel(modal, new ResourceModel("editRole", "Edit role"), new CompoundPropertyModel<>(new RoleDTO(model.getObject()))) {
                    @Override
                    protected void onUpdate(final Role role, final AjaxRequestTarget target) {
                        model.setObject(role);
                        target.add(wmc);
                    }
                });
                modal.show(target);
            }
        };
        btnGroup.add(editRoleBtn);

        final AjaxLink removeRoleBtn = new AjaxLink("removeRoleBtn") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new BootstrapModalConfirmationPanel(modal, new ResourceModel("removeRole", "Remove role"),
                        new ResourceModel("removeRoleQuestion")) {
                    @Override
                    protected void onConfirm(final AjaxRequestTarget target) {
                        roleService.removeRole(model.getObject());
                        RolePanel.this.onRemove(target);
                    }
                });
                modal.show(target);
            }
        };
        btnGroup.add(removeRoleBtn);

        final SimpleDataProvider<Person, String> dataProvider = new SimpleDataProvider<Person, String>(roleService.getPersons4Role(model.getObject())) {
            @Override
            public String getDefaultSort() {
                return "firstName";
            }
        };

        final List<IColumn<Person, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(Model.of("ID"), "id", "id"));
        columns.add(new PropertyColumn<>(new ResourceModel("firstName", "Given name"), "firstName", "firstName"));
        columns.add(new PropertyColumn<>(new ResourceModel("lastName", "Surname"), "lastName", "lastName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "email", "email"));

        final BootstrapAjaxDataTable<Person, String> dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 10);
        dataTable.condensed().hover();
        wmc.add(dataTable);
    }

    private void onPermissionsBtnClick(final IModel<Role> model, final AjaxRequestTarget target) {
        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new EditPermissionPanel<>(modal, new CompoundPropertyModel<>(new EditRolePermissionDTO(model.getObject()))));
        modal.show(target);
    }

    protected abstract void onRemove(final AjaxRequestTarget target);
}
