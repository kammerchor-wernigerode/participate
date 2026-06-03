package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import org.apache.wicket.model.IModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Streamable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpringDataProviderTests {

    @Mock
    private Streamable<String> streamable;

    private SpringDataProvider<String> provider;

    @BeforeEach
    void setUp() {
        provider = new SpringDataProvider<>() {

            @Override
            public Streamable<String> streamable(long first, long count) {
                return streamable;
            }

            @Override
            public long size() {
                return 0;
            }
        };
    }

    @Test
    void iterator_shouldDelegateToStreamableIterator() {
        Iterator<String> iterator = List.of("a", "b").iterator();
        when(streamable.iterator()).thenReturn(iterator);

        assertThat(provider.iterator(0, 10)).isSameAs(iterator);
    }

    @Test
    void iterator_shouldPassFirstAndCountToStreamable() {
        when(streamable.iterator()).thenReturn(Collections.emptyIterator());

        provider.iterator(5, 20);

        verify(streamable).iterator();
    }

    @Test
    void model_shouldReturnModelWrappingTheObject() {
        IModel<String> model = provider.model("test");

        assertThat(model.getObject()).isEqualTo("test");
    }

    @Test
    void model_withNull_shouldReturnModelWrappingNull() {
        IModel<String> model = provider.model(null);

        assertThat(model.getObject()).isNull();
    }
}
