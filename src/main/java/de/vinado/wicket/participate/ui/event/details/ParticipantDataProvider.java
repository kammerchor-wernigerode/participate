package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.repeater.table.FilterableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ParticipantDataProvider extends FilterableDataProvider<Participant> {

    private final IModel<Event> event;
    private final EventService eventService;
    private final PersonContext selfSupplier;

    public ParticipantDataProvider(IModel<Event> event,
                                   EventService eventService,
                                   IModel<ParticipantFilter> filterModel) {
        this(event, eventService, filterModel, () -> null);
    }

    public ParticipantDataProvider(IModel<Event> event,
                                   EventService eventService,
                                   IModel<ParticipantFilter> filterModel,
                                   PersonContext selfSupplier) {
        super(filterModel);
        this.event = event;
        this.eventService = eventService;
        this.selfSupplier = selfSupplier;
    }

    @Override
    public Iterator<? extends Participant> iterator(long first, long count) {
        Person self = selfSupplier.get();
        return load()
            .filter(filter())
            .sorted(Comparator.comparing(keyExtractor(), keyComparator()))
            .sorted(Comparator.comparing(people(person -> Objects.equals(person, self)), Comparator.reverseOrder()))
            .skip(first).limit(count)
            .iterator();
    }

    @Override
    protected Stream<Participant> load() {
        return eventService.getParticipants(event.getObject()).stream();
    }

    private static Function<Participant, Boolean> people(Function<Person, Boolean> keyExtractor) {
        return keyExtractor.compose(Participant::getSinger);
    }

    @Override
    public IModel<Participant> model(Participant participant) {
        return new ParticipantModel(participant);
    }


    private static final class ParticipantModel extends Model<Participant> {

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
                participant.getAccommodation(),
                participant.getCarSeatCount(),
                participant.getComment()
            );
        }
    }

    @Override
    public void detach() {
        event.detach();
        super.detach();
    }
}
