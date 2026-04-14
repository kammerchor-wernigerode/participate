package de.kammerchorwernigerode.app.participate.event.presentation.ui.edit;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecord;
import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.components.form.EventForm;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDto;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.EventsPage;
import de.kammerchorwernigerode.app.participate.wicket.ModelNotFoundException;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

public class EventEditPage extends ParticipatePage implements IGenericComponent<EventDto, EventEditPage> {

    @SpringBean
    private EventRecordRepository eventRecordRepository;

    public EventEditPage(PageParameters parameters) {
        super(parameters);

        setModel(new EventDtoModel(parameters));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventDto> model = getModel();
        IModel<EventDto> formModel = model.flatMap(CompoundPropertyModel::new);
        EventEditForm eventEditForm = new EventEditForm("eventForm", formModel);
        add(eventEditForm);
    }


    private static class EventEditForm extends EventForm {

        public EventEditForm(String id, IModel<EventDto> model) {
            super(id, model);
        }

        @Override
        protected void onSubmit() {
            setResponsePage(EventsPage.class);
        }
    }


    @RequiredArgsConstructor
    private class EventDtoModel extends LoadableDetachableModel<EventDto> {

        private final PageParameters parameters;

        @Override
        protected EventDto load() {
            StringValue idParam = parameters.get("id");
            EventRecord record = findEventRecord(idParam);
            return translate(record);
        }

        private EventRecord findEventRecord(StringValue idParam) {
            try {
                Long eventId = idParam.toOptionalLong();
                return Optional.ofNullable(eventId)
                    .flatMap(eventRecordRepository::findById)
                    .orElseThrow();
            } catch (Exception e) {
                throw new ModelNotFoundException("Event w/ id=" + idParam + " could not be found", e);
            }
        }

        private EventDto translate(EventRecord record) {
            EventDto model = new EventDto();
            model.setId(record.getId());
            model.setSummary(record.getSummary());
            model.setStartDateTime(LocalDateTime.ofInstant(record.getStartInstant(), record.getStartZoneId()));
            model.setStartZoneId(record.getStartZoneId());
            model.setEndDateTime(LocalDateTime.ofInstant(record.getEndInstant(), record.getEndZoneId()));
            model.setEndZoneId(record.getEndZoneId());
            model.setLocation(record.getLocation());
            model.setDescription(record.getDescription());
            return model;
        }
    }
}
