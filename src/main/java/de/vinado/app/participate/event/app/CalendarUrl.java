package de.vinado.app.participate.event.app;

import de.vinado.wicket.participate.model.Event;

import java.net.URI;
import java.util.Locale;
import java.util.function.BiFunction;

public interface CalendarUrl extends BiFunction<Event, Locale, URI> {
}
