package de.vinado.wicket.participate.common.generator;

import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.services.DataService;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventGenerator extends EntityGenerator<EventDTO> {

    private static EventGenerator instance = new EventGenerator();

    private String[] eventTypes = {""};

    private String[] localities = {""};


    private EventGenerator() {
    }

    public static EventGenerator getInstance() {
        return instance;
    }

    @Override
    public EventDTO generate(final DataService dataService) {
        final EventDTO dto = new EventDTO();
        dto.setEventType(randomString(eventTypes));
        dto.setStartDate(generateDate());
        dto.setEndDate(DateUtils.addDays(dto.getStartDate(), rint(0, 7)));
        dto.setLocation(randomString(localities));

        return dto;
    }
}
