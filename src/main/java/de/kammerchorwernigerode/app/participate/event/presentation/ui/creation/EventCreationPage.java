package de.kammerchorwernigerode.app.participate.event.presentation.ui.creation;

import de.kammerchorwernigerode.app.participate.event.presentation.components.form.EventForm;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDto;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.EventsPage;
import de.kammerchorwernigerode.app.participate.wicket.ParticipatePage;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class EventCreationPage extends ParticipatePage {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        EventDto eventDto = new EventDto();
        IModel<EventDto> model = new CompoundPropertyModel<>(eventDto);
        EventCreationForm eventFormPanel = new EventCreationForm("eventForm", model);
        add(eventFormPanel);
    }


    private static class EventCreationForm extends EventForm {

        public EventCreationForm(String id, IModel<EventDto> model) {
            super(id, model);
        }

        @Override
        protected void onSubmit() {
            setResponsePage(EventsPage.class);
            String message = getString("EventCreationForm.submit.success");
            getSession().success(message);
        }
    }
}
