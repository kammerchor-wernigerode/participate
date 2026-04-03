package de.kammerchorwernigerode.app.participate.event.infrastructure;

import de.kammerchorwernigerode.app.participate.common.uuidv5.UuidV5Generator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AttendeeDomainKeyFactory extends UuidV5Generator<AttendeeRecord.Id> {

    public AttendeeDomainKeyFactory() {
        super(AttendeeRecord.Id::toUri);
    }

    public UUID create(AttendeeRecord.Id id) {
        return generate(id);
    }
}
