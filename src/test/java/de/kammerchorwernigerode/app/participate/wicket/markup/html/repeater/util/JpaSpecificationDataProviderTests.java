package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.AbstractJpaSpecificationDataProviderTests.TestSpec;
import org.apache.wicket.model.IModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor.SpecificationFluentQuery;
import org.springframework.data.util.Streamable;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaSpecificationDataProviderTests {

    @Mock
    private IModel<TestSpec> specModel;

    @Mock
    private SpringSortState<String> sortState;

    @Mock
    private JpaSpecificationExecutor<String> executor;

    @Mock
    private TestSpec spec;

    @Mock
    private SpecificationFluentQuery<String> fluentQuery;

    @Mock
    private Slice<String> slice;

    private JpaSpecificationDataProvider<String, String, TestSpec> provider;

    @BeforeEach
    void setUp() {
        provider = new JpaSpecificationDataProvider<>(specModel, sortState, executor);
    }

    @Test
    void project_shouldReturnQueryUnchanged() {
        when(sortState.getSort()).thenReturn(Sort.unsorted());
        when(specModel.getObject()).thenReturn(spec);
        when(fluentQuery.slice(any())).thenReturn(slice);
        doAnswer(i -> {
            Function<SpecificationFluentQuery<String>, Slice<String>> fn = i.getArgument(1);
            return fn.apply(fluentQuery);
        }).when(executor).findBy(eq(spec), any());

        provider.streamable(0L, 10L);

        verify(fluentQuery).slice(any(Pageable.class));
    }

    @Test
    void streamable_shouldDelegateToExecutor() {
        when(sortState.getSort()).thenReturn(Sort.unsorted());
        when(specModel.getObject()).thenReturn(spec);
        doReturn(slice).when(executor).findBy(eq(spec), any());

        Streamable<String> result = provider.streamable(0L, 10L);

        assertThat(result).isSameAs(slice);
    }

    @Test
    void size_shouldCountViaExecutor() {
        when(specModel.getObject()).thenReturn(spec);
        when(executor.count(spec)).thenReturn(3L);

        assertThat(provider.size()).isEqualTo(3L);
    }
}
