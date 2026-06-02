package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventSummarySuggestionQuery {

    List<String> execute(Payload payload);


    record Payload(String query, Pageable pageable) {
    }
}
