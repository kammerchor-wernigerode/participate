package de.vinado.wicket.participate.wicket.form.ui;

import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Accommodation.Status;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AccommodationFormGroupTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders()
            .add(new ClassStringResourceLoader(AccommodationFormGroup.class));
    }

    @Test
    void starting_shouldRenderComponent() {
        Accommodation accommodation = mock(Accommodation.class, RETURNS_DEEP_STUBS);
        when(accommodation.getStatus()).thenReturn(Status.SEARCHING);

        AccommodationFormGroup component = new AccommodationFormGroup("control", () -> accommodation);
        tester.startComponentInPage(component);

        tester.assertComponent("control", AccommodationFormGroup.class);
    }
}
