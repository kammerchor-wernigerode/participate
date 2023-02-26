package de.vinado.wicket.participate.ui.administration.person;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.repeater.table.FunctionalDataProvider;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PersonAdministrationPanel extends Panel {

    private static final long serialVersionUID = 2120435783576012933L;

    @SpringBean
    private PersonRepository personRepository;

    public PersonAdministrationPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(deletedPersonTable("deleted"));
    }

    private WebMarkupContainer deletedPersonTable(String id) {
        PersonDataProvider dataProvider = new PersonDataProvider(personRepository);
        List<? extends IColumn<Person, SerializableFunction<Person, ?>>> columns = columns();
        return new PersonAdministrationTable(id, columns, dataProvider);
    }

    private List<? extends IColumn<Person, SerializableFunction<Person, ?>>> columns() {
        ArrayList<IColumn<Person, SerializableFunction<Person, ?>>> columns = new ArrayList<>();
        return columns;
    }


    @RequiredArgsConstructor
    private static class PersonDataProvider extends FunctionalDataProvider<Person> {

        private static final long serialVersionUID = 8241723874342037449L;

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

            private static final long serialVersionUID = -2205664904469033653L;

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