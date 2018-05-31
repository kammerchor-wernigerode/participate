package de.vinado.wicket.participate.ui.member.groups;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.BootstrapPanel;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.dto.GroupDTO;
import de.vinado.wicket.participate.data.dto.MemberToGroupDTO;
import de.vinado.wicket.participate.event.GroupUpdateEvent;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class GroupsPanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private Form form;

    public GroupsPanel(final String id) {
        super(id);

        form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        final ListView<Group> groups = new ListView<Group>("groups", new LoadableDetachableModel<List<Group>>() {
            @Override
            protected List<Group> load() {
                return personService.getVisibleGroupList();
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Group> item) {
                final BootstrapPanel<Group> groupPanel = new BootstrapPanel<Group>("group",
                        new CompoundPropertyModel<>(item.getModelObject()), new PropertyModel<>(item.getModel(), "title")) {
                    @Override
                    protected Panel newBodyPanel(final String id, final IModel<Group> model) {
                        return new GroupPanel(id, model);
                    }

                    @Override
                    protected AbstractLink newDefaultBtn(final String id, final IModel<Group> model) {
                        setDefaultBtnLabelModel(new ResourceModel("addPerson", "Add person"));
                        setDefaultBtnIcon(FontAwesomeIconType.plus);
                        return new AjaxLink<Group>(id, model) {
                            @Override
                            public void onClick(final AjaxRequestTarget target) {
                                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                                modal.setContent(new AddMemberToGroupPanel(modal,
                                        new ResourceModel("addMemberToGroup", "Add member to group"),
                                        new CompoundPropertyModel<>(new MemberToGroupDTO(
                                                model.getObject(), personService.getGroupMemberList(model.getObject())))));
                                modal.show(target);
                            }
                        };
                    }

                    @Override
                    protected RepeatingView newDropDownMenu(final String id, final IModel<Group> groupModel) {
                        final RepeatingView dropDownMenu = super.newDropDownMenu(id, groupModel);
                        dropDownMenu.add(new DropDownItem(dropDownMenu.newChildId(), new ResourceModel("editGroup", "Edit group"), FontAwesomeIconType.edit) {
                            @Override
                            protected void onClick(final AjaxRequestTarget target) {
                                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                                modal.setContent(new AddEditGroupPanel(modal, new ResourceModel("editGroup", "Edit Group"), new CompoundPropertyModel<>(new GroupDTO(groupModel.getObject()))) {
                                    @Override
                                    protected void onUpdate(final Group savedGroup, final AjaxRequestTarget target) {
                                        target.add(form);
                                        Snackbar.show(target, new ResourceModel("editGroupA"));
                                    }
                                });
                                modal.show(target);
                            }
                        });
                        return dropDownMenu;
                    }

                    @Override
                    protected void onConfigure() {
                        setDefaultModelObject(item.getModelObject());
                    }
                };
                item.setOutputMarkupId(true);
                item.add(groupPanel);
            }
        };
        groups.setOutputMarkupId(true);
        wmc.add(groups);

        final BootstrapAjaxLink createGroupBtn = new BootstrapAjaxLink("createGroupBtn", Buttons.Type.Primary) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new AddEditGroupPanel(modal, new ResourceModel("addGroup", "Add group"), new CompoundPropertyModel<>(new GroupDTO())) {
                    @Override
                    protected void onUpdate(final Group savedGroup, final AjaxRequestTarget target) {
                        Snackbar.show(target, new ResourceModel("addGroupS", "A new group has been created"));
                        target.add(form);
                    }
                });
                modal.show(target);
            }
        };
        createGroupBtn.setLabel(new ResourceModel("addGroup", "Add Group"));
        createGroupBtn.setIconType(FontAwesomeIconType.plus);
        createGroupBtn.setSize(Buttons.Size.Small);
        wmc.add(createGroupBtn);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        final Object payload = event.getPayload();
        if (payload instanceof GroupUpdateEvent) {
            final GroupUpdateEvent updateEvent = (GroupUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            target.add(form);
        }
    }
}
