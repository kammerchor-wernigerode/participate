package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class EventFilterFormTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        List<IStringResourceLoader> loaders = tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders();
        loaders.add(new ClassStringResourceLoader(EventFilterForm.class));
    }

    @Test
    void starting_shouldRenderComponent() {
        EventFilterForm component = new TestEventFilterForm("form", EventFilter::new);
        tester.startComponentInPage(component);

        tester.assertComponent("form", EventFilterForm.class);
    }


    private class TestEventFilterForm extends EventFilterForm {

        public TestEventFilterForm(String id, IModel<EventFilter> model) {
            super(id, model);
        }

        @Override
        protected void onApply() {
        }
    }
}
