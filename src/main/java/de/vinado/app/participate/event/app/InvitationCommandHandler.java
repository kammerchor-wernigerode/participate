package de.vinado.app.participate.event.app;

import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.notification.email.app.EmailService;
import de.vinado.app.participate.notification.email.model.Email;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.notification.email.model.TemplatedEmailFactory;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.MessageSource;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.vinado.app.participate.notification.email.app.SendEmail.send;
import static de.vinado.app.participate.notification.email.model.Recipient.to;
import static java.util.Comparator.comparing;

@Component
@RequiredArgsConstructor
public class InvitationCommandHandler {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final @NonNull EventService eventService;
    private final @NonNull MessageSource messageSource;
    private final @NonNull CalendarUrl calendarUrl;
    private final @NonNull TemplatedEmailFactory emailFactory;
    private final @NonNull EmailService emailService;
    private final @NonNull ApplicationProperties properties;
    private final @NonNull Printer<EventName> eventNamePrinter;
    private final @NonNull UnaryOperator<String> link = this::createFormUrl;
    private final @NonNull Function<Event, EventName> eventName = this::createFromEvent;

    public void execute(@NonNull SendBulkInvitations command) throws EmailException {
        List<Event> events = command.events();

        if (events.size() == 1) {
            Event event = events.get(0);
            List<Participant> participants = eventService.getInvitedParticipants(event);
            eventService.inviteParticipants(participants);
            return;
        }

        MultiValueMap<Singer, Item> participants = events.stream()
            .map(eventService::getParticipants)
            .flatMap(List::stream)
            .collect(LinkedMultiValueMap::new, this::index, MultiValueMap::addAll);

        for (Map.Entry<Singer, List<Item>> entry : participants.entrySet()) {
            List<Item> items = new ArrayList<>(entry.getValue());
            if (areAllDefinite(items)) {
                continue;
            }

            Singer singer = entry.getKey();
            Collections.sort(items);
            Locale locale = Locale.getDefault();
            invite(singer, items, locale);
            update(items);
        }
    }

    private boolean areAllDefinite(List<Item> items) {
        return items.stream()
                .map(Item::getParticipant)
                .map(Participant::getInvitationStatus)
                .allMatch(InvitationStatus::isDefinite);
    }

    private void index(LinkedMultiValueMap<Singer, Item> map, Participant participant) {
        map.add(participant.getSinger(), new Item(participant.getEvent(), participant));
    }

    private void invite(Singer singer, List<Item> items, Locale locale) throws EmailException {
        List<Event> events = items.stream().map(Item::getEvent).distinct().collect(Collectors.toList());
        String subject = subject(events, locale);
        Map<String, Object> data = templateData(subject, singer, items);
        Email email = emailFactory.create(subject, "invitation.bulk.txt.ftl", "invitation.bulk.html.ftl", data, locale);
        emailService.execute(send(email).atOnce(to(singer)));
    }

    private String subject(List<Event> events, Locale locale) {
        Object[] args = subjectArgs(events, locale);
        return isMonthSpan(events)
            ? messageSource.getMessage("event.invitations.message.subject.range", args, locale)
            : messageSource.getMessage("event.invitations.message.subject.single", args, locale);
    }

    private boolean isMonthSpan(List<Event> events) {
        return events.stream()
            .map(Event::getLocalStartDate)
            .map(LocalDate::getMonth)
            .distinct()
            .count() > 1;
    }

    private Object[] subjectArgs(List<Event> events, Locale locale) {
        Assert.notEmpty(events, "events must not be empty");
        events.sort(comparing(Event::getLocalStartDate));
        Event first = events.get(0);
        Event last = events.get(events.size() - 1);

        DateTimeFormatter formatter = MONTH_FORMATTER.withLocale(locale);
        return new Object[]{
            formatter.format(first.getLocalStartDate()),
            formatter.format(last.getLocalStartDate()),
        };
    }

    private Map<String, Object> templateData(String title, Singer singer, List<Item> items) {
        Assert.notEmpty(items, "items must not be empty");
        Collections.sort(items);

        HashMap<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("singer", singer);
        data.put("items", items);
        data.put("link", link);
        data.put("calendarUrl", calendarUrl);
        data.put("eventName", eventName);
        data.put("printer", eventNamePrinter);
        return data;
    }

    private void update(List<Item> items) {
        items.stream()
            .map(Item::getParticipant)
            .distinct()
            .filter(Participant::isUninvited)
            .peek(participant -> participant.setInvitationStatus(InvitationStatus.PENDING))
            .forEach(eventService::saveParticipant);
    }

    private String createFormUrl(String token) {
        return UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
            .path("/form/participant")
            .queryParam("token", token)
            .toUriString();
    }

    private EventName createFromEvent(Event event) {
        return EventName.of(event);
    }


    @Value
    public static class Item implements Comparable<Item> {

        Event event;
        Participant participant;

        @Override
        public int compareTo(@NonNull Item that) {
            Comparator<Event> comparator = comparing(Event::getLocalStartDate);
            return comparing(Item::getEvent, comparator).compare(this, that);
        }
    }
}
