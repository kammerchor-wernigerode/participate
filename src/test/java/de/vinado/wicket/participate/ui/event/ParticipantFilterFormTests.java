package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ParticipantFilterFormTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        List<IStringResourceLoader> loaders = tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders();
        loaders.add(new ClassStringResourceLoader(ParticipantFilterForm.class));
    }

    @Test
    void starting_shouldRenderComponent() {
        ParticipantFilterForm component = new TestParticipantFilterForm("id", ParticipantFilter::new);
        tester.startComponentInPage(component);

        tester.assertComponent("id", ParticipantFilterForm.class);
    }


    private class TestParticipantFilterForm extends ParticipantFilterForm {

        public TestParticipantFilterForm(String id, IModel<ParticipantFilter> model) {
            super(id, model);
        }

        @Override
        protected void onApply() {
        }
    }
}
