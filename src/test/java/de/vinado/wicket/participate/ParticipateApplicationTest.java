package de.vinado.wicket.participate;

import de.vinado.wicket.participate.ui.event.EventsPage;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Ignore
public class ParticipateApplicationTest {

    private WicketTester tester;

    @Before
    public void setUp() {
        tester = new WicketTester(new ParticipateApplication());
    }

    @Test
    public void testHomePageRendering() {
        tester.startPage(EventsPage.class);
        tester.assertRenderedPage(EventsPage.class);
    }
}
