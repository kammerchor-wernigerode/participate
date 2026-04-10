package de.kammerchorwernigerode.app.participate.event.presentation.ui.details;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

public class EventDetailsPage extends ParticipatePage implements IGenericComponent<EventEntry, EventDetailsPage> {

    @SpringBean
    private EventEntryRepository eventEntryRepository;

    public EventDetailsPage(PageParameters parameters) {
        super(parameters);

        setModel(new EventEntryModel(parameters, eventEntryRepository));
    }


    @RequiredArgsConstructor
    private static class EventEntryModel extends LoadableDetachableModel<EventEntry> {

        private final PageParameters parameters;
        private final EventEntryRepository eventEntryRepository;

        @Override
        protected EventEntry load() {
            StringValue idParam = parameters.get("id");
            return findEventEntry(idParam);
        }

        private EventEntry findEventEntry(StringValue idParam) {
            try {
                Long eventId = idParam.toOptionalLong();
                return Optional.ofNullable(eventId)
                    .flatMap(eventEntryRepository::findById)
                    .orElseThrow();
            } catch (Exception e) {
                throw new ModelNotFoundException("Event w/ id=" + idParam + " could not be found", e);
            }
        }
    }
}
