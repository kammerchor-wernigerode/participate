package de.vinado.wicket.tabs;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.WicketTestCase;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LambdaTabTests extends WicketTestCase {

    @Mock
    private IModel<String> model;

    @Mock
    private SerializableFunction<String, WebMarkupContainer> constructor;

    private LambdaTab tab;

    @BeforeEach
    void setUp() {
        tab = new LambdaTab(model, constructor);
    }

    @Test
    void givenNullDependencies_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new LambdaTab(null, constructor));
        assertThrows(IllegalArgumentException.class, () -> new LambdaTab(model, null));
    }

    @Test
    void gettingPanel_shouldApplyConstructor() {
        when(constructor.apply("foo")).thenReturn(new EmptyPanel("foo"));

        tab.getPanel("foo");

        verify(constructor).apply("foo");
    }
}
