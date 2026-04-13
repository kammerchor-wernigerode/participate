package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry_;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.CompoundJpaDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.Objects;

public class AttendeeDataProvider extends CompoundJpaDataProvider<AttendeeEntry> {

    public AttendeeDataProvider(AttendeeEntryRepository attendeeEntryRepository,
                                IModel<AttendeeEntrySpecification> filterState) {
        super(attendeeEntryRepository, filterState.map(SerializableFunction.identity()));
    }

    @Override
    public Sort getSort() {
        Sort primary = super.getSort();
        Sort secondary = JpaSort.of(Direction.ASC, AttendeeEntry_.invitationStatusOrder, AttendeeEntry_.voiceOrder,
            AttendeeEntry_.fileName, AttendeeEntry_.firstName, AttendeeEntry_.lastName);
        return primary.and(secondary);
    }

    @Override
    public IModel<AttendeeEntry> model(AttendeeEntry entry) {
        return new AttendeeEntryModel(entry);
    }


    private static class AttendeeEntryModel extends Model<AttendeeEntry> {

        public AttendeeEntryModel(AttendeeEntry entry) {
            super(entry);
        }

        @Override
        public int hashCode() {
            AttendeeEntry entry = getObject();
            return Objects.hash(
                entry.getInvitationStatus(),
                entry.getFirstName(),
                entry.getLastName(),
                entry.getFileName(),
                entry.getVoice());
        }
    }
}
