package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.dto.EventDTO;
import de.vinado.wicket.participate.service.DataService;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventGenerator extends AbstractEntityGenerator<EventDTO> {

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
        dto.setEndDate(addDays(dto.getStartDate(), rint(0, 7)));
        dto.setLocation(randomString(localities));
        dto.setGroup(dataService.load(Group.class, 1L));

        return dto;
    }
}
