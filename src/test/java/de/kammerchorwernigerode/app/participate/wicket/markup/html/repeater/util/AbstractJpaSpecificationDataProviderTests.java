package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.SpringSortState;
import org.apache.wicket.model.IModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor.SpecificationFluentQuery;
import org.springframework.data.util.Streamable;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractJpaSpecificationDataProviderTests {

    @Mock
    private IModel<TestSpec> specModel;

    @Mock
    private SpringSortState<String> sortState;

    @Mock
    private JpaSpecificationExecutor<String> executor;

    @Mock
    private TestSpec spec;

    @Mock
    private Slice<String> slice;

    private AbstractJpaSpecificationDataProvider<String, String, String, TestSpec> provider;

    @BeforeEach
    void setUp() {
        provider = new AbstractJpaSpecificationDataProvider<>(specModel, sortState, executor) {

            @Override
            protected SpecificationFluentQuery<String> project(SpecificationFluentQuery<String> query) {
                return query;
            }
        };
    }

    @Test
    void getFilterState_shouldReturnObjectFromSpecModel() {
        when(specModel.getObject()).thenReturn(spec);

        assertThat(provider.getFilterState()).isSameAs(spec);
    }

    @Test
    void getFilterState_whenModelReturnsNull_shouldReturnNull() {
        when(specModel.getObject()).thenReturn(null);

        assertThat(provider.getFilterState()).isNull();
    }

    @Test
    void setFilterState_shouldDelegateToSpecModel() {
        provider.setFilterState(spec);

        verify(specModel).setObject(spec);
    }

    @Test
    void setFilterState_withNull_shouldDelegateNullToSpecModel() {
        provider.setFilterState(null);

        verify(specModel).setObject(null);
    }

    @Test
    void size_shouldCountViaExecutorUsingCurrentFilterState() {
        when(specModel.getObject()).thenReturn(spec);
        when(executor.count(spec)).thenReturn(42L);

        assertThat(provider.size()).isEqualTo(42L);
    }

    @Test
    void size_withExplicitSpec_shouldDelegateToExecutorCount() {
        when(executor.count(spec)).thenReturn(7L);

        assertThat(provider.size(spec)).isEqualTo(7L);
    }

    @Test
    void size_withNullSpec_shouldDelegateToExecutorCount() {
        when(executor.count((TestSpec) null)).thenReturn(0L);

        assertThat(provider.size(null)).isEqualTo(0L);
    }

    @Test
    void detach_shouldDetachSpecModel() {
        provider.detach();

        verify(specModel).detach();
    }

    @Test
    void streamable_withPageable_shouldUseCurrentFilterState() {
        when(specModel.getObject()).thenReturn(spec);
        doReturn(slice).when(executor).findBy(eq(spec), any());

        Streamable<String> result = provider.streamable(Pageable.unpaged());

        assertThat(result).isSameAs(slice);
        verify(specModel).getObject();
    }

    @Test
    void streamable_withExplicitSpec_shouldCallFindByWithThatSpec() {
        doReturn(slice).when(executor).findBy(eq(spec), any());

        Streamable<String> result = provider.streamable(spec, Pageable.unpaged());

        assertThat(result).isSameAs(slice);
        verify(executor).findBy(eq(spec), any());
    }

    @Test
    void streamable_withNullSpec_shouldCallFindByWithNull() {
        doReturn(slice).when(executor).findBy(isNull(TestSpec.class), any());

        Streamable<String> result = provider.streamable(null, Pageable.unpaged());

        assertThat(result).isSameAs(slice);
    }


    interface TestSpec extends Specification<String>, Serializable {
    }
}
