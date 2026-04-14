package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EventDto implements Serializable {

    private String summary;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String location;
    private String description;
}
