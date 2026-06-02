package de.kammerchorwernigerode.app.participate.event.presentation.api;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bff/events")
@RequiredArgsConstructor
class EventBffController {

    private final EventRecordRepository eventRecordRepository;

    @GetMapping("/summaries/suggestions")
    public ResponseEntity<List<String>> suggestSummaries(@RequestParam String q) {
        if (!StringUtils.hasText(q)) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> suggestions = eventRecordRepository.findSuggestions(q, PageRequest.of(0, 5));
        return ResponseEntity.ok(suggestions);
    }
}
