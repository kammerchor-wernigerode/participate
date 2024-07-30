package de.vinado.app.participate.event.app;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.ZoneId;

@Value
@ConfigurationProperties("app.google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarUrlProperties {

    /**
     * Source query parameter 'src' of the Google Calendar URL
     */
    URI source;

    /**
     * Timezone query parameter 'ctz' of the Google Calendar URL (optional)
     */
    @Nullable
    ZoneId timezone;
}
