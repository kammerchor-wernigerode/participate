package de.vinado.wicket.participate.ui.administration.person;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.person.presentation.ui.PersonRestorationService;
import de.vinado.wicket.repeater.table.FunctionalDataProvider;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PersonAdministrationPanel extends Panel {

    @SpringBean
    private PersonRepository personRepository;

    @SpringBean
    private PersonRestorationService personRestorationService;

    public PersonAdministrationPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(deletedPersonTable("deleted"));
    }

    private WebMarkupContainer deletedPersonTable(String id) {
        PersonDataProvider dataProvider = dataProvider();
        List<? extends IColumn<Person, SerializableFunction<Person, ?>>> columns = columns();
        return new PersonAdministrationTable(id, columns, dataProvider);
    }

    private PersonDataProvider dataProvider() {
        PersonDataProvider dataProvider = new PersonDataProvider(personRepository);
        dataProvider.setSort(Person::getSortName, SortOrder.ASCENDING);
        return dataProvider;
    }

    private List<? extends IColumn<Person, SerializableFunction<Person, ?>>> columns() {
        ArrayList<IColumn<Person, SerializableFunction<Person, ?>>> columns = new ArrayList<>();
        columns.add(nameColumn());
        columns.add(lastModifiedColumn());
        columns.add(restoreColumn());
        return columns;
    }

    private IColumn<Person, SerializableFunction<Person, ?>> nameColumn() {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), Person::getSortName, "sortName");
    }

    private IColumn<Person, SerializableFunction<Person, ?>> lastModifiedColumn() {
        return new PropertyColumn<>(new ResourceModel("person.deleted", "Deleted"), Person::getLastModified, "lastModified");
    }

    private IColumn<Person, SerializableFunction<Person, ?>> restoreColumn() {
        return new BootstrapAjaxLinkColumn<>(FontAwesome6IconType.trash_arrow_up_s, new ResourceModel("person.restore", "Restore")) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Person> rowModel) {
                restore(target, rowModel);
            }

            @Override
            public String getCssClass() {
                return "restore";
            }
        };
    }

    private void restore(AjaxRequestTarget target, IModel<Person> rowModel) {
        IModel<String> successMessage = restoreConfirmationMessage(rowModel);
        personRestorationService.restore(rowModel.getObject());
        send(this, Broadcast.BREADTH, new PersonAdministrationTable.UpdateIntent());
        Snackbar.show(target, successMessage);
    }

    private IModel<String> restoreConfirmationMessage(IModel<Person> model) {
        Person person = model.getObject();
        return new StringResourceModel("person.restore.message.success", model)
            .setDefaultValue(person.getDisplayName() + "has been restored and invitations to upcoming events have been sent out.");
    }


    @RequiredArgsConstructor
    private static class PersonDataProvider extends FunctionalDataProvider<Person> {

        private final PersonRepository repository;

        @Override
        protected Stream<Person> load() {
            return repository.listInactivePersons();
        }

        @Override
        public IModel<Person> model(Person person) {
            return new PersonModel(person);
        }

        private static class PersonModel extends Model<Person> {

            public PersonModel(Person person) {
                super(person);
            }

            @Override
            public int hashCode() {
                Person person = getObject();
                return Objects.hash(
                    person.getId(),
                    person.getFirstName(),
                    person.getLastName(),
                    person.getEmail()
                );
            }
        }
    }
}
