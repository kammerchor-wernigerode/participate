package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class ParticipantDataProvider extends SortableDataProvider<Participant, SerializableFunction<Participant, ?>> {

    private final IModel<Event> event;
    private final EventService eventService;
    private final IModel<ParticipantFilter> filterModel;

    @Override
    public Iterator<? extends Participant> iterator(long first, long count) {
        return streamFilteredParticipants()
            .skip(first).limit(count)
            .sorted(Comparator.comparing(keyExtractor(), comparator()))
            .iterator();
    }

    private SerializableFunction<Participant, String> keyExtractor() {
        return getSort().getProperty().andThen(Object::toString);
    }

    private Comparator<String> comparator() {
        return getSort().isAscending() ? Comparator.naturalOrder() : Comparator.reverseOrder();
    }

    @Override
    public long size() {
        return streamFilteredParticipants().count();
    }

    private Stream<Participant> streamFilteredParticipants() {
        return eventService.getParticipants(event.getObject())
            .stream()
            .filter(filterModel.getObject());
    }

    @Override
    public IModel<Participant> model(Participant participant) {
        return new ParticipantModel(participant);
    }


    private static final class ParticipantModel extends Model<Participant> {

        private static final long serialVersionUID = -7306487774730058888L;

        public ParticipantModel(Participant participant) {
            super(participant);
        }

        @Override
        public int hashCode() {
            Participant participant = getObject();
            return Objects.hash(
                participant.getId(),
                participant.getEvent(),
                participant.getToken(),
                participant.getSinger(),
                participant.getInvitationStatus(),
                participant.getFromDate(),
                participant.getToDate(),
                participant.isCatering(),
                participant.isAccommodation(),
                participant.getCarSeatCount(),
                participant.getComment()
            );
        }
    }

    @Override
    public void detach() {
        filterModel.detach();
        super.detach();
    }
}
