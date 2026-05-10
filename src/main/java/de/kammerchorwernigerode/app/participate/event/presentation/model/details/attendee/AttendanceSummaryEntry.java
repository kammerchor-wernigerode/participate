package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import java.io.Serializable;

public record AttendanceSummaryEntry(
    Long id,
    int attendeeCount,
    int requiredBedCount,
    int permanentCount,
    int maxCount) implements Serializable {
}
