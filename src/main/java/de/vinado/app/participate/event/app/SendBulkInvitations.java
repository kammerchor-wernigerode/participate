package de.vinado.app.participate.event.app;

import de.vinado.wicket.participate.model.Event;
import lombok.NonNull;

import java.util.List;

public record SendBulkInvitations(@NonNull List<Event> events) {
}
