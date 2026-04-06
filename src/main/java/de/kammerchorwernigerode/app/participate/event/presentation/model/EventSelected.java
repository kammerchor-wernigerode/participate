package de.kammerchorwernigerode.app.participate.event.presentation.model;

import org.apache.wicket.util.io.IClusterable;

public record EventSelected(Long eventId) implements IClusterable {
}
