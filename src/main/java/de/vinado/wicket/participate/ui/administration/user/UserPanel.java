package de.vinado.wicket.participate.ui.administration.user;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalConfirmationPanel;
import de.vinado.wicket.participate.components.panels.BootstrapAjaxLinkPanel;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
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
public class UserPanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    private BootstrapAjaxDataTable<User, String> dataTable;

    public UserPanel(final String id) {
        super(id);

        final SimpleDataProvider<User, String> dataProvider =
            new SimpleDataProvider<User, String>(userService.getAll()) {
                @Override
                public String getDefaultSort() {
                    return "id";
                }
            };

        final List<IColumn<User, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("user.id", "User ID"), "id", "id"));
        columns.add(new PropertyColumn<>(new ResourceModel("username", "Username"), "username", "username"));
        columns.add(new PropertyColumn<User, String>(new ResourceModel("administrator", "Administrator"), "admin", "admin") {
            @Override
            public void populateItem(final Item<ICellPopulator<User>> item, final String componentId, final IModel<User> rowModel) {
                item.add(new IconPanel(
                    componentId,
                    rowModel.getObject().isAdmin() ? FontAwesome5IconType.check_s : FontAwesome5IconType.times_s,
                    IconPanel.Color.DEFAULT, TextAlign.CENTER));
            }

            @Override
            public String getCssClass() {
                return "width-fix-70";
            }
        });
        columns.add(new PropertyColumn<User, String>(new ResourceModel("enabled", "Enabled"), "enabled", "enabled") {
            @Override
            public void populateItem(final Item<ICellPopulator<User>> item, final String componentId, final IModel<User> rowModel) {
                item.add(new IconPanel(
                    componentId,
                    rowModel.getObject().isEnabled() ? FontAwesome5IconType.check_s : FontAwesome5IconType.times_s,
                    IconPanel.Color.DEFAULT, TextAlign.CENTER));
            }

            @Override
            public String getCssClass() {
                return "width-fix-70";
            }
        });
        columns.add(new AbstractColumn<User, String>(Model.of()) {
            @Override
            public void populateItem(final Item<ICellPopulator<User>> item, final String componentId, final IModel<User> rowModel) {
                final boolean person = null != rowModel.getObject().getPerson();
                final BootstrapAjaxLinkPanel button = new BootstrapAjaxLinkPanel(
                    componentId, Buttons.Type.Link, person ? FontAwesome5IconType.trash_alt_s : FontAwesome5IconType.plus_s) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                        if (null != rowModel.getObject().getPerson()) {
                            modal.setContent(new BootstrapModalConfirmationPanel(modal,
                                new ResourceModel("user.remove.person", "Remove user-person association"),
                                new ResourceModel("user.remove.person.question", "Are you sure you want to remove the user-person association?")) {
                                @Override
                                protected void onConfirm(final AjaxRequestTarget target) {
                                    final AddUserDTO dto = new AddUserDTO(rowModel.getObject());
                                    dto.setPerson(null);
                                    userService.saveUser(dto);
                                    dataProvider.set(userService.getUsers());
                                    target.add(dataTable);
                                }
                            });
                        } else {
                            modal.setContent(new AddPersonToUserPanel(modal, new CompoundPropertyModel<>(new AddUserDTO(rowModel.getObject()))) {
                                @Override
                                protected void onConfirm(final User savedUser, final AjaxRequestTarget target) {
                                    dataProvider.set(userService.getAll());
                                    if (userService.startPasswordReset(savedUser.getPerson().getEmail(), true)) {
                                        Snackbar.show(target, new ResourceModel("email.send.invitation.success", "An invitation has been sent"));
                                    } else {
                                        Snackbar.show(target, new ResourceModel("email.send.invitation.error", "There was an error sending the invitation"));
                                    }
                                    target.add(dataTable);
                                }
                            });
                        }
                        modal.show(target);
                    }
                };
                item.add(button);
            }
        });
        columns.add(new PropertyColumn<>(new ResourceModel("person.id", "Person ID"), "person.id", "person.id"));
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "person.lastName", "person.lastName"));
        columns.add(new PropertyColumn<>(new ResourceModel("firstName", "Given name"), "person.firstName", "person.firstName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "person.email", "person.email"));
        columns.add(new AbstractColumn<User, String>(Model.of("")) {
            @Override
            public void populateItem(final Item<ICellPopulator<User>> item, final String componentId, final IModel<User> rowModel) {
                final Person person = rowModel.getObject().getPerson();
                if (null != person) {
                    item.add(new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, FontAwesome5IconType.edit_s) {
                        @Override
                        public void onClick(final AjaxRequestTarget target) {
                            ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                            modal.setContent(new AddEditPersonPanel(modal, new ResourceModel("person.edit", "Edit Person"),
                                new CompoundPropertyModel<>(new PersonDTO(person))) {
                                @Override
                                protected void onUpdate(final AjaxRequestTarget target) {
                                    dataProvider.set(userService.getAll());
                                    target.add(dataTable);
                                    Snackbar.show(target, new ResourceModel("person.edit.success", "The person was successfully edited"));
                                }
                            });
                            modal.show(target);
                        }
                    });
                } else {
                    item.add(new EmptyPanel(componentId));
                }
            }
        });

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 30);
        dataTable.setOutputMarkupId(true);
        dataTable.hover().condensed();
        add(dataTable);

        final BootstrapAjaxLink<Void> createUserBtn = new BootstrapAjaxLink<>("createUserBtn", Buttons.Type.Default) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                modal.setContent(new AddUserPanel(modal, new CompoundPropertyModel<>(new AddUserDTO())) {
                    @Override
                    protected void onConfirm(final User user, final AjaxRequestTarget target) {
                        dataProvider.set(userService.getAll());
                        target.add(dataTable);
                        Snackbar.show(target, new ResourceModel("user.add.success", "A new user has been added"));
                    }
                });
                modal.show(target);
            }
        };
        createUserBtn.add(new CssClassNameAppender(Model.of("pull-right")));
        createUserBtn.setLabel(new ResourceModel("user.add", "Add User"));
        createUserBtn.setSize(Buttons.Size.Small);
        createUserBtn.setIconType(FontAwesome5IconType.plus_s);
        add(createUserBtn);
    }
}
