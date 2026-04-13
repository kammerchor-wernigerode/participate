package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntryRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntrySpecification;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry_;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.CompoundJpaDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.Objects;

public class PersonDataProvider extends CompoundJpaDataProvider<PersonEntry> {

    public static final String[] NAME_SORT_PROPERTY = {
        "fileName",
        "firstName",
        "lastName",
    };

    public PersonDataProvider(PersonEntryRepository personEntryRepository,
                              IModel<PersonEntrySpecification> filterState) {
        super(personEntryRepository, filterState.map(SerializableFunction.identity()));
    }

    public Sort getSort() {
        Sort primary = super.getSort();
        Sort secondary = JpaSort.of(Direction.ASC, PersonEntry_.voiceOrder,
            PersonEntry_.fileName, PersonEntry_.firstName, PersonEntry_.lastName);
        return primary.and(secondary);
    }

    @Override
    public IModel<PersonEntry> model(PersonEntry entry) {
        return new PersonEntryModel(entry);
    }


    private static class PersonEntryModel extends Model<PersonEntry> {

        public PersonEntryModel(PersonEntry entry) {
            super(entry);
        }

        @Override
        public int hashCode() {
            PersonEntry entry = getObject();
            return Objects.hash(
                entry.getFirstName(),
                entry.getLastName(),
                entry.getFileName(),
                entry.getEmailAddress(),
                entry.getVoice());
        }
    }
}
