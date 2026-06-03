package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.JpaSpecificationDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Objects;

public class AttendeeDetailsDataProvider
    extends JpaSpecificationDataProvider<AttendeeDetailsEntry, String[], AttendeeDetailsSpecification> {

    public AttendeeDetailsDataProvider(IModel<AttendeeDetailsSpecification> filterState,
                                       SpringSortState<String[]> sortState,
                                       AttendeeDetailsEntryRepository attendeeDetailsEntryRepository) {
        super(filterState, sortState, attendeeDetailsEntryRepository);
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
