package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

class DetailedParticipantFilterFormTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        List<IStringResourceLoader> loaders = tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders();
        loaders.add(new ClassStringResourceLoader(DetailedParticipantFilterForm.class));
    }

    @Test
    void starting_shouldRenderComponent() {
        Event event = createEvent();
        DetailedParticipantFilterForm component = new TestDetailedParticipantFilterForm("id", ParticipantFilter::new, () -> event);
        tester.startComponentInPage(component);

        tester.assertComponent("id", DetailedParticipantFilterForm.class);
    }

    private static Event createEvent() {
        Event event = mock(Event.class);
        when(event.getStartDate()).thenReturn(new Date());
        when(event.getEndDate()).thenReturn(new Date());
        return event;
    }


    private class TestDetailedParticipantFilterForm extends DetailedParticipantFilterForm {

        public TestDetailedParticipantFilterForm(String id, IModel<ParticipantFilter> model, IModel<Event> event) {
            super(id, model, event);
        }

        @Override
        protected void onApply() {
        }
    }
}
