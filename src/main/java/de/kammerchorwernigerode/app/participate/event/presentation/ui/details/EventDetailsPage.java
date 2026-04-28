package de.kammerchorwernigerode.app.participate.event.presentation.ui.details;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

public class EventDetailsPage extends ParticipatePage implements IGenericComponent<Long, EventDetailsPage> {

    @SpringBean
    private EventRecordRepository eventRecordRepository;

    public EventDetailsPage(PageParameters parameters) {
        super(parameters);

        setModel(new EventIdEntryModel(parameters, eventRecordRepository));
    }


    @RequiredArgsConstructor
    private static class EventIdEntryModel extends LoadableDetachableModel<Long> {

        private final PageParameters parameters;
        private final EventRecordRepository eventRecordRepository;

        @Override
        protected Long load() {
            StringValue idParam = parameters.get("id");
            return findEventEntry(idParam);
        }

        private Long findEventEntry(StringValue idParam) {
            try {
                Long eventId = idParam.toOptionalLong();
                return Optional.ofNullable(eventId)
                    .filter(eventRecordRepository::existsById)
                    .orElseThrow();
            } catch (Exception e) {
                throw new ModelNotFoundException("Event w/ id=" + idParam + " could not be found", e);
            }
        }
    }
}
