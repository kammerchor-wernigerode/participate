package de.vinado.wicket.participate.ui.administration.role;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Collapsible;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.data.dto.RoleDTO;
import de.vinado.wicket.participate.data.permission.Role;
import de.vinado.wicket.participate.service.RoleService;
import de.vinado.wicket.participate.service.UserService;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
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
public class RolesPanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    @SpringBean
    @SuppressWarnings("unused")
    private RoleService roleService;

    public RolesPanel(final String id) {
        super(id);

        final IModel<List<Role>> model = new CompoundPropertyModel<>(roleService.getRoles());
        setDefaultModel(model);

        final Form form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("rolesWmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        final List<ITab> tabs = new ArrayList<>();
        for (Role role : model.getObject()) {
            tabs.add(new AbstractTab(Model.of(role.getName())) {
                @Override
                public Panel getPanel(final String panelId) {
                    return new RolePanel(panelId, new CompoundPropertyModel<>(role), role.isEditable()) {
                        @Override
                        protected void onRemove(final AjaxRequestTarget target) {
                            model.setObject(roleService.getRoles());
                            target.add(wmc);
                            Snackbar.show(target, new ResourceModel("removeRoleConf"));
                        }
                    };
                }
            });
        }

        final Collapsible collapsible = new Collapsible("collapsible", tabs);
        wmc.add(collapsible);

        final BootstrapAjaxLink createRoleBtn = new BootstrapAjaxLink("createRoleBtn", Buttons.Type.Primary) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new AddEditRolePanel(modal, new ResourceModel("addRole", "Add Role"), new CompoundPropertyModel<>(new RoleDTO())) {
                    @Override
                    protected void onUpdate(final Role role, final AjaxRequestTarget target) {
                        model.setObject(roleService.getRoles());
                        target.add(wmc);
                        Snackbar.show(target, new ResourceModel("addRoleConf"));
                    }
                });
                modal.show(target);
            }
        };
        createRoleBtn.setIconType(FontAwesomeIconType.plus);
        createRoleBtn.setLabel(new ResourceModel("addRole", "Add role"));
        createRoleBtn.setSize(Buttons.Size.Small);
        form.add(createRoleBtn);
    }
}
