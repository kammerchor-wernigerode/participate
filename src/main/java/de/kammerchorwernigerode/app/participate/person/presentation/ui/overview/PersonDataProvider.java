package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.data.domain.OffsetPageRequest;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntryRepository;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntrySpecification;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry_;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonDataProvider implements ISortableDataProvider<PersonEntry, String[]>,
    IFilterStateLocator<PersonEntrySpecification> {

    public static final String[] NAME_SORT_PROPERTY = {
        "fileName",
        "firstName",
        "lastName",
    };

    @Getter
    private final SortState sortState = new SortState();

    private final PersonEntryRepository personEntryRepository;
    private final IModel<PersonEntrySpecification> filterState;

    @Override
    public Iterator<? extends PersonEntry> iterator(long first, long count) {
        Specification<PersonEntry> spec = getFilterState();

        Sort sort = getSort();
        Pageable pageable = new OffsetPageRequest(first, count, sort);

        Page<PersonEntry> page = personEntryRepository.findAll(spec, pageable);
        List<PersonEntry> content = page.getContent();
        return content.iterator();
    }

    public Sort getSort() {
        Sort primary = sortState.getSort();
        Sort secondary = JpaSort.of(Direction.ASC, PersonEntry_.voiceOrder,
            PersonEntry_.fileName, PersonEntry_.firstName, PersonEntry_.lastName);
        return primary.and(secondary);
    }

    public void setPropertySortOrder(@NonNull String[] properties, @NonNull SortOrder sortOrder) {
        sortState.setPropertySortOrder(properties, sortOrder);
    }

    @Override
    public long size() {
        Specification<PersonEntry> spec = getFilterState();
        return personEntryRepository.count(spec);
    }

    @Override
    public PersonEntrySpecification getFilterState() {
        return filterState.getObject();
    }

    @Override
    public void setFilterState(PersonEntrySpecification state) {
        filterState.setObject(state);
    }

    @Override
    public IModel<PersonEntry> model(PersonEntry entry) {
        return new PersonEntryModel(entry);
    }

    @Override
    public void detach() {
        filterState.detach();
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
