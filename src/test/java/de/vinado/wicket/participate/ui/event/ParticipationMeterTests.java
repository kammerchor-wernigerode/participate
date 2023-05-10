package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.EventDetails;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class ParticipationMeterTests extends WicketTestCase {

    private static final String ID = "meter";

    @Test
    void withoutEvent_shouldRenderComponent() {
        tester.startComponentInPage(new ParticipationMeter(ID, Model.of()));

        tester.assertComponent(ID, ParticipationMeter.class);
    }

    @ParameterizedTest
    @MethodSource("sections")
    void renderingComponent_shouldRenderSections(String sectionId) {
        tester.startComponentInPage(new ParticipationMeter(ID, Model.of()));

        tester.assertComponent(ID + ":" + sectionId, Label.class);
    }

    @Test
    void renderingComponent_shouldPrintAcceptedSum() {
        int count = randomInt();
        EventDetails event = mock(EventDetails.class);
        when(event.getAcceptedSum()).thenReturn(Long.valueOf(count));

        tester.startComponentInPage(new ParticipationMeter(ID, () -> event));

        tester.assertContains(String.valueOf(count));
    }

    @Test
    void renderingComponent_shouldPrintDeclinedCount() {
        int count = randomInt();
        EventDetails event = mock(EventDetails.class);
        when(event.getDeclinedCount()).thenReturn(Long.valueOf(count));

        tester.startComponentInPage(new ParticipationMeter(ID, () -> event));

        tester.assertContains(String.valueOf(count));
    }

    @Test
    void renderingComponent_shouldPrintPendingCount() {
        int count = randomInt();
        EventDetails event = mock(EventDetails.class);
        when(event.getPendingCount()).thenReturn(Long.valueOf(count));

        tester.startComponentInPage(new ParticipationMeter(ID, () -> event));

        tester.assertContains(String.valueOf(count));
    }

    private static int randomInt() {
        Random random = new Random();
        return random.nextInt();
    }

    private static Arguments[] sections() {
        return Stream.of("a", "d", "p")
            .map(Arguments::of)
            .toArray(Arguments[]::new);
    }
}
