package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.model.dtos.EventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.string.Strings;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Slf4j
public class ParticipateUtils {

    public static String getGenericEventName(EventDTO dto, Locale locale) {
        String genericName;
        if (dto.isSeveralDays()) {
            genericName = new SimpleDateFormat("yyyy-MM.dd.-", locale).format(dto.getStartDate())
                + (new SimpleDateFormat("MM", locale).format(dto.getEndDate()).equals(new SimpleDateFormat("MM", locale).format(dto.getStartDate())) ? "" : new SimpleDateFormat("MM", locale).format(dto.getEndDate()) + ".")
                + new SimpleDateFormat("dd", locale).format(dto.getEndDate()) + ". "
                + new SimpleDateFormat("MMMM", locale).format(dto.getStartDate()) + " "
                + dto.getEventType()
                + (!Strings.isEmpty(dto.getLocation()) ? (" in " + dto.getLocation()) : "");
        } else {
            genericName = new SimpleDateFormat("yyyy-MM.dd. MMMM", locale).format(dto.getStartDate()) + " "
                + dto.getEventType()
                + (!Strings.isEmpty(dto.getLocation()) ? (" in " + dto.getLocation()) : "");
        }
        return genericName;
    }

    public static URL generateInvitationLink(String baseUrl, String token) {
        try {
            URL url = new URL(baseUrl + "/form/participant?token=" + token);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Malformed URL", e);
            return null;
        }
    }
}
