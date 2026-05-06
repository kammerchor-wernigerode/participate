package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.CompoundJpaDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Objects;

public class AttendeeDetailsDataProvider
    extends CompoundJpaDataProvider<AttendeeDetailsEntry, AttendeeDetailsSpecification> {

    public AttendeeDetailsDataProvider(AttendeeDetailsEntryRepository attendeeDetailsEntryRepository,
                                       IModel<AttendeeDetailsSpecification> filterState) {
        super(attendeeDetailsEntryRepository, filterState);
    }

    @Override
    public IModel<AttendeeDetailsEntry> model(AttendeeDetailsEntry entry) {
        return new AttendeeDetailModel(entry);
    }


    private static class AttendeeDetailModel extends Model<AttendeeDetailsEntry> {

        public AttendeeDetailModel(AttendeeDetailsEntry entry) {
            super(entry);
        }

        @Override
        public int hashCode() {
            AttendeeDetailsEntry entry = getObject();
            return Objects.hash(
                entry.getId(),
                entry.getFileName(),
                entry.getFirstName(),
                entry.getLastName(),
                entry.getInvitationStatus(),
                entry.getVoice(),
                entry.isAccommodationNeeded(),
                entry.getBedsOfferedCount(),
                entry.isByCar(),
                entry.getCarSeatCount(),
                entry.getComment(),
                entry.getInvitationStatusOrder(),
                entry.getVoiceOrder());
        }
    }
}
