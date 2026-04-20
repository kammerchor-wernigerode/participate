package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Data;

@Data
public class DateRange implements Serializable {

    private LocalDateTime startDateTime;
    private ZoneId startZoneId = ZoneId.systemDefault();
    private LocalDateTime endDateTime;
    private ZoneId endZoneId = ZoneId.systemDefault();
}
