package de.vinado.wicket.participate.ui.administration.user;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.ConfirmationModal;
import de.vinado.wicket.bt4.modal.Modal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.panels.BootstrapAjaxLinkPanel;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.panels.IconPanel.Color;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.primitives.Ints.saturatedCast;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.check_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.edit_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.plus_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.times_s;
import static de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType.trash_alt_s;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class UserPanel extends BootstrapPanel<Void> {

    @SpringBean
    private UserService userService;

    public UserPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addQuickAccessAction(AjaxAction.create(new ResourceModel("user.add", "Add User"), plus_s, this::add));

        add(new BootstrapAjaxDataTable<>("dataTable", columns(), dataProvider(), Integer.MAX_VALUE)
            .hover().condensed()
            .setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance())
            .setOutputMarkupId(true)
            .add(new CssClassNameAppender("users"))
            .add(new UpdateOnEventBehavior<>(UserTableUpdateIntent.class)));
    }

    private UserDataProvider dataProvider() {
        UserDataProvider dataProvider = new UserDataProvider(userService);
        dataProvider.setSort(with(User::getId).andThen(repeat('0')), SortOrder.ASCENDING);
        return dataProvider;
    }

    private SerializableFunction<Long, String> repeat(char ch) {
        return userId -> StringUtils.repeat(ch, saturatedCast(userId));
    }

    private void add(AjaxRequestTarget target) {
        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new AddUserPanel(modal, new CompoundPropertyModel<>(new AddUserDTO())) {

            @Override
            protected void onConfirm(User user, AjaxRequestTarget target) {
                send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
                Snackbar.show(target, new ResourceModel("user.add.success", "A new user has been added"));
            }
        });
        modal.show(target);
    }

    private List<IColumn<User, SerializableFunction<User, ?>>> columns() {
        return Arrays.asList(
            idColumn(),
            usernameColumn(),
            adminColumn(),
            enabledColumn(),
            assignColumn(),
            personIdColumn(),
            nameColumn(),
            emailColumn(),
            editColumn()
        );
    }

    private IColumn<User, SerializableFunction<User, ?>> idColumn() {
        return new PropertyColumn<>(new ResourceModel("user.id", "User ID"), with(User::getId).andThen(repeat('0')), "id") {

            @Override
            public String getCssClass() {
                return "id";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> usernameColumn() {
        return new PropertyColumn<>(new ResourceModel("username", "Username"), with(User::getUsername), "username") {

            @Override
            public String getCssClass() {
                return "username";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> adminColumn() {
        return new PropertyColumn<>(new ResourceModel("administrator", "Administrator"), with(User::isAdmin), "admin") {

            @Override
            public void populateItem(Item<ICellPopulator<User>> item, String componentId, IModel<User> rowModel) {
                item.add(new IconPanel(
                    componentId,
                    rowModel.getObject().isAdmin() ? check_s : times_s,
                    Color.DEFAULT, TextAlign.CENTER));
            }

            @Override
            public String getCssClass() {
                return "admin";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> enabledColumn() {
        return new PropertyColumn<>(new ResourceModel("enabled", "Enabled"), with(User::isEnabled), "enabled") {

            @Override
            public void populateItem(Item<ICellPopulator<User>> item, String componentId, IModel<User> rowModel) {
                item.add(new IconPanel(
                    componentId,
                    rowModel.getObject().isEnabled() ? check_s : times_s,
                    Color.DEFAULT, TextAlign.CENTER));
            }

            @Override
            public String getCssClass() {
                return "enabled";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> assignColumn() {
        return new AbstractColumn<>(Model.of()) {

            @Override
            public void populateItem(Item<ICellPopulator<User>> item, String componentId, IModel<User> rowModel) {
                FontAwesome5IconType icon = isAssociated(rowModel.getObject())
                    ? trash_alt_s
                    : plus_s;
                BootstrapAjaxLinkPanel button = new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, icon) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                        modal.setContent(isAssociated(rowModel.getObject())
                            ? unassociate(modal)
                            : associate(modal));
                        modal.show(target);
                    }

                    private Modal<?> unassociate(ModalAnchor modal) {
                        return new ConfirmationModal(modal,
                            new ResourceModel("user.remove.person.question", "Are you sure you want to remove the user-person association?")) {

                            @Override
                            protected void onConfirm(AjaxRequestTarget target) {
                                unassign(rowModel.getObject());
                                send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());

                            }

                            private void unassign(User user) {
                                AddUserDTO dto = new AddUserDTO(user);
                                dto.setPerson(null);
                                userService.saveUser(dto);
                            }
                        }.title(new ResourceModel("user.remove.person", "Remove user-person association"));
                    }

                    private Modal<?> associate(ModalAnchor modal) {
                        return new AddPersonToUserPanel(modal, new CompoundPropertyModel<>(new AddUserDTO(rowModel.getObject()))) {

                            @Override
                            protected void onConfirm(User savedUser, AjaxRequestTarget target) {
                                Person person = savedUser.getPerson();
                                assignAndNotify(person, target);
                                send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
                            }

                            private void assignAndNotify(Person person, AjaxRequestTarget target) {
                                try {
                                    assign(person);
                                    Snackbar.show(target, new ResourceModel("email.send.invitation.success", "An invitation has been sent"));
                                } catch (RuntimeException e) {
                                    Snackbar.show(target, new ResourceModel("email.send.invitation.error", "There was an error sending the invitation"));
                                }
                            }

                            private void assign(Person person) {
                                boolean success = userService.startPasswordReset(person.getEmail(), true);
                                if (!success) {
                                    throw new RuntimeException();
                                }
                            }
                        };
                    }
                };
                item.add(button);
            }

            private boolean isAssociated(User user) {
                return null != user.getPerson();
            }

            @Override
            public String getCssClass() {
                return "association";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> personIdColumn() {
        return new PropertyColumn<>(new ResourceModel("person.id", "Person ID"), withPerson().andThen(nullSafe(Person::getId)), "person.id") {

            @Override
            public String getCssClass() {
                return "person-id";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> nameColumn() {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), withPerson().andThen(nullSafe(Person::getSortName)), "person.sortName") {

            @Override
            public String getCssClass() {
                return "person-name";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> emailColumn() {
        return new PropertyColumn<>(new ResourceModel("email", "Email"), withPerson().andThen(nullSafe(Person::getEmail)), "person.email") {

            @Override
            public String getCssClass() {
                return "person-email";
            }
        };
    }

    private IColumn<User, SerializableFunction<User, ?>> editColumn() {
        return new AbstractColumn<>(Model.of("")) {

            @Override
            public void populateItem(Item<ICellPopulator<User>> item, String componentId, IModel<User> rowModel) {
                Person person = rowModel.getObject().getPerson();
                if (null == person) {
                    item.add(new EmptyPanel(componentId));
                    return;
                }

                item.add(new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, edit_s) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
                        modal.setContent(editPerson(modal));
                        modal.show(target);
                    }

                    private AddEditPersonPanel editPerson(ModalAnchor modal) {
                        return new AddEditPersonPanel(modal, new ResourceModel("person.edit", "Edit Person"),
                            new CompoundPropertyModel<>(new PersonDTO(person))) {

                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
                                Snackbar.show(target, new ResourceModel("person.edit.success", "The person was successfully edited"));
                            }
                        };
                    }
                });
            }

            @Override
            public String getCssClass() {
                return "person-edit";
            }
        };
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }

    private static <T, R> SerializableFunction<T, R> nullSafe(SerializableFunction<T, R> extractor) {
        return person -> Optional.ofNullable(person)
            .map(extractor)
            .orElse(null);
    }

    private static SerializableFunction<User, Person> withPerson() {
        return with(User::getPerson);
    }
}
