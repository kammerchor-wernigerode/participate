package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.Data;

@Data
public class EventDto implements Serializable {

    private Long id;
    private String summary;
    private LocalDateTime startDateTime;
    private ZoneId startZoneId = ZoneId.systemDefault();
    private LocalDateTime endDateTime;
    private ZoneId endZoneId = ZoneId.systemDefault();
    private String location;
    private String description;
}
