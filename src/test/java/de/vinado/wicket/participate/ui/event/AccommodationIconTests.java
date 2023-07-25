package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.test.SpringEnabledWicketTestCase;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.mockito.Mockito.*;

class AccommodationIconTests extends SpringEnabledWicketTestCase {

    @BeforeEach
    void setUp() {
        tester.getApplication()
            .getResourceSettings()
            .getStringResourceLoaders()
            .add(new ClassStringResourceLoader(AccommodationIcon.class));
    }

    @ParameterizedTest
    @EnumSource(Accommodation.Status.class)
    void starting_shouldRenderComponent(Accommodation.Status status) {
        Accommodation accommodation = mock(Accommodation.class, RETURNS_DEEP_STUBS);
        when(accommodation.getStatus()).thenReturn(status);

        AccommodationIcon component = new AccommodationIcon("icon", () -> accommodation);
        tester.startComponentInPage(component);

        tester.assertComponent("icon", AccommodationIcon.class);
    }
}
