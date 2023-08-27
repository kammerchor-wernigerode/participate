package de.vinado.wicket.participate.ui.event;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

class MailtoUriBuilderTests {

    private static final String FROM = "john.doe@example.com";

    @Test
    void queryingMultipleBcc_shouldAppendKeyValuePairs() {
        String email1 = "jane.deo@example.com";
        String email2 = "max.mustermann@example.com";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("mailto:")
            .queryParam("to", FROM)
            .queryParam("bcc", email1)
            .queryParam("bcc", email2);

        assertEquals("mailto:?to=" + FROM + "&bcc=" + email1 + "&bcc=" + email2, builder.toUriString());
    }
}
