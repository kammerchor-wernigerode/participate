package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface AttendeeProjection extends Serializable {

    LocalDateTime getFromDateTime();

    LocalDateTime getToDateTime();
}
