package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventSummarySuggestionProvider {

    List<String> findSuggestions(String query, Pageable pageable);
}
