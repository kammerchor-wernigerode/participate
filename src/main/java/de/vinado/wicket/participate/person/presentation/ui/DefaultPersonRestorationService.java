package de.vinado.wicket.participate.person.presentation.ui;

import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.singer.model.SingerRepository;
import de.vinado.wicket.participate.user.model.UserContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class DefaultPersonRestorationService implements PersonRestorationService {

    private final @NonNull PersonRepository personRepository;
    private final @NonNull EventService eventService;
    private final @NonNull SingerRepository singerRepository;
    private final @NonNull UserContext userContext;

    @Override
    public void restore(@NonNull Person person) {
        personRepository.restore(person);

        invite(person);
    }

    private void invite(Person person) {
        Singer singer = singerRepository.findBy(person.getId()).orElseThrow(IllegalStateException::new);
        List<Participant> participants = eventService.getUpcomingEvents().stream()
            .map(event -> eventService.createParticipant(event, singer))
            .collect(Collectors.toList());

        eventService.inviteParticipants(participants, userContext.get());
    }
}
