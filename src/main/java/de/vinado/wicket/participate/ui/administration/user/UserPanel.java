package de.vinado.wicket.participate.ui.administration.user;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
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

public class UserPanel extends BootstrapPanel<Void> {

    @SpringBean
    private UserService userService;

    private final de.vinado.app.participate.wicket.bt5.modal.Modal modal;

    public UserPanel(String id) {
        super(id);

        this.modal = modal("modal");
    }

    protected de.vinado.app.participate.wicket.bt5.modal.Modal modal(String wicketId) {
        return new de.vinado.app.participate.wicket.bt5.modal.Modal(wicketId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(modal);

        addQuickAccessAction(AjaxAction.create(new ResourceModel("user.add", "Add User"), FontAwesome6IconType.plus_s, this::add));

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
        IModel<AddUserDTO> model = new CompoundPropertyModel<>(new AddUserDTO());

        modal
            .setHeaderVisible(true)
            .title(new ResourceModel("user.add", "Add User"))
            .content(id -> new AddUserPanel(id, model))
            .addCloseAction(new ResourceModel("cancel", "Cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), this::onConfirm)
            .show(target);
    }

    private void onConfirm(AjaxRequestTarget target) {
        send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
        Snackbar.show(target, new ResourceModel("user.add.success", "A new user has been added"));
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
                    rowModel.getObject().isAdmin() ? FontAwesome6IconType.check_s : FontAwesome6IconType.xmark_s,
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
                    rowModel.getObject().isEnabled() ? FontAwesome6IconType.check_s : FontAwesome6IconType.xmark_s,
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
                FontAwesome6IconType icon = isAssociated(rowModel.getObject())
                    ? FontAwesome6IconType.trash_arrow_up_s
                    : FontAwesome6IconType.plus_s;
                BootstrapAjaxLinkPanel button = new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, icon) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        boolean associated = isAssociated(rowModel.getObject());
                        if (associated) {
                            unassociate(target);
                        } else {
                            associate(target);
                        }
                    }

                    private void unassociate(AjaxRequestTarget target) {
                        IModel<String> prompt = new ResourceModel("user.remove.person.question", "Are you sure you want to remove the user-person association?");

                        modal
                            .setHeaderVisible(false)
                            .content(id -> new SmartLinkMultiLineLabel(id, prompt))
                            .addCloseAction(new ResourceModel("abort", "Abort"))
                            .addAction(id -> new BootstrapAjaxLink<Void>(id, Buttons.Type.Success) {

                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    unassign(rowModel.getObject());
                                    send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());

                                    modal.close(target);
                                }

                                private void unassign(User user) {
                                    AddUserDTO dto = new AddUserDTO(user);
                                    dto.setPerson(null);
                                    userService.saveUser(dto);
                                }
                            }.setLabel(new ResourceModel("confirm", "Confirm")))
                            .show(target);
                    }

                    private void associate(AjaxRequestTarget target) {
                        IModel<AddUserDTO> model = new CompoundPropertyModel<>(new AddUserDTO(rowModel.getObject()));

                        modal
                            .setHeaderVisible(true)
                            .title(new ResourceModel("person.assign", "Assign Person"))
                            .content(id -> new AddPersonToUserPanel(id, model))
                            .addCloseAction(new ResourceModel("cancel", "Cancel"))
                            .addSubmitAction(new ResourceModel("save", "Save"), this::onConfirm)
                            .show(target);
                    }

                    private void onConfirm(AjaxRequestTarget target) {
                        Snackbar.show(target, new ResourceModel("email.send.invitation.success", "An invitation has been sent"));
                        send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
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

                item.add(new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, FontAwesome6IconType.pencil_s) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        IModel<PersonDTO> model = new CompoundPropertyModel<>(new PersonDTO(person));

                        modal
                            .setHeaderVisible(true)
                            .title(new ResourceModel("person.edit", "Edit Person"))
                            .content(id -> new AddEditPersonPanel(id, model))
                            .addCloseAction(new ResourceModel("cancel", "Cancel"))
                            .addSubmitAction(new ResourceModel("save", "Save"), this::onUpdate)
                            .show(target);
                    }

                    private void onUpdate(AjaxRequestTarget target) {
                        send(getWebPage(), Broadcast.BREADTH, new UserTableUpdateIntent());
                        Snackbar.show(target, new ResourceModel("person.edit.success", "The person was successfully edited"));
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
