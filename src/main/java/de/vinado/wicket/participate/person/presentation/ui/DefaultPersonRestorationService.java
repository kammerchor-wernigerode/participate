package de.vinado.wicket.participate.person.presentation.ui;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.singer.model.SingerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class DefaultPersonRestorationService implements PersonRestorationService {

    private final @NonNull PersonRepository personRepository;
    private final @NonNull EventService eventService;
    private final @NonNull SingerRepository singerRepository;

    @Override
    public void restore(@NonNull Person person) {
        personRepository.restore(person);

        invite(person);
    }

    private void invite(Person person) {
        Singer singer = singerRepository.findBy(person.getId()).orElseThrow(IllegalStateException::new);
        List<Participant> participants = eventService.getUpcomingEvents().stream()
            .map(createParticipant(singer))
            .peek(setInvitationStatus(InvitationStatus.UNINVITED))
            .collect(Collectors.toList());

        eventService.inviteParticipants(participants);
    }

    private Function<Event, Participant> createParticipant(Singer singer) {
        return event -> createParticipant(event, singer);
    }

    private Participant createParticipant(Event event, Singer singer) {
        return eventService.getParticipants(singer).stream()
            .filter(by(event))
            .findFirst()
            .orElseGet(() -> eventService.createParticipant(event, singer));
    }

    private static Predicate<Participant> by(Event event) {
        return participant -> event.equals(participant.getEvent());
    }

    private static Consumer<Participant> setInvitationStatus(InvitationStatus uninvited) {
        return participant -> participant.setInvitationStatus(uninvited);
    }
}
