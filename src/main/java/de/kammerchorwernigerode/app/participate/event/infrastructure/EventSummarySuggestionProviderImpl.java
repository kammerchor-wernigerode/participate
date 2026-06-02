package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.domain.Pageable;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EventSummarySuggestionProviderImpl implements EventSummarySuggestionProvider {

    private final EventSummarySuggestionQuery eventSummarySuggestionQuery;

    @Override
    public List<String> findSuggestions(String query, Pageable pageable) {
        EventSummarySuggestionQuery.Payload payload = new EventSummarySuggestionQuery.Payload(query, pageable);
        return eventSummarySuggestionQuery.execute(payload);
    }
}
