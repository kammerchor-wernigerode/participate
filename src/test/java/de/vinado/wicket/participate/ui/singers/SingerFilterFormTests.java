package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.participate.model.filters.SingerFilter;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.model.IModel;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SingerFilterFormTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders()
            .add(new ClassStringResourceLoader(SingerFilterForm.class));
    }

    @Test
    void starting_shouldRenderComponent() {
        SingerFilterForm component = new TestSingerFilterForm("id", SingerFilter::new);
        tester.startComponentInPage(component);

        tester.assertComponent("id", SingerFilterForm.class);
    }


    private class TestSingerFilterForm extends SingerFilterForm {

        public TestSingerFilterForm(String id, IModel<SingerFilter> model) {
            super(id, model);
        }

        @Override
        protected void onApply() {
        }
    }
}
