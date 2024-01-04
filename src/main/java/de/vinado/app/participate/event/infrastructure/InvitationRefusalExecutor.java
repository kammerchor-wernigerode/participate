package de.vinado.app.participate.event.infrastructure;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.singer.model.SingerRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
class InvitationRefusalExecutor implements ApplicationListener<ApplicationReadyEvent> {

    @NonNull
    private final SingerRepository singerRepository;
    @NonNull
    private final EventService eventService;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        singerRepository.listInactiveSingers()
            .map(eventService::getParticipants)
            .flatMap(List::stream)
            .filter(byUpcoming(Participant::getEvent))
            .map(ParticipantDTO::new)
            .forEach(eventService::declineEvent);
    }

    private static Predicate<Participant> byUpcoming(Function<Participant, Event> extractor) {
        return extractor.andThen(Event::isUpcoming)::apply;
    }
}
