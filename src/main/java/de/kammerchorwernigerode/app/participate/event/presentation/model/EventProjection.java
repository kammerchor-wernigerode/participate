package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventReference;

import java.io.Serializable;

public interface EventProjection extends EventReference, EventDates, Serializable {

    String getSummary();
}
