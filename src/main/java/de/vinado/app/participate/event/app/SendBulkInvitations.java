package de.vinado.app.participate.event.app;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import lombok.NonNull;

import java.util.List;
import java.util.function.Predicate;

public record SendBulkInvitations(@NonNull List<Event> events, @NonNull Predicate<Participant> filter) {
}
