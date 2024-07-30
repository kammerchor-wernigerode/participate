package de.vinado.wicket.participate.services;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class FrequencyMapTests {

    @Test
    void withoutDuplicatesFrequency_shouldBeOne() {
        List<String> elements = List.of("a", "b", "c");
        Map<String, Integer> frequency = elements.stream()
            .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum));

        assertThat(frequency)
            .containsEntry("a", 1)
            .containsEntry("b", 1)
            .containsEntry("c", 1);
    }

    @Test
    void withDuplicateFrequency_shouldAddUp() {
        List<String> elements = List.of("a", "b", "a", "c", "b");
        Map<String, Integer> frequency = elements.stream()
            .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum));

        assertThat(frequency)
            .containsEntry("a", 2)
            .containsEntry("b", 2)
            .containsEntry("c", 1);
    }
}
