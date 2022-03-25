package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.model.dtos.EventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.string.Strings;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class ParticipateUtils {

    public static String getGenericEventName(final EventDTO dto) {
        String genericName;
        if (dto.isSeveralDays()) {
            genericName = new SimpleDateFormat("yyyy-MM.dd.-").format(dto.getStartDate())
                + (new SimpleDateFormat("MM").format(dto.getEndDate()).equals(new SimpleDateFormat("MM").format(dto.getStartDate())) ? "" : new SimpleDateFormat("MM").format(dto.getEndDate()) + ".")
                + new SimpleDateFormat("dd").format(dto.getEndDate()) + ". "
                + new SimpleDateFormat("MMMM").format(dto.getStartDate()) + " "
                + dto.getEventType()
                + (!Strings.isEmpty(dto.getLocation()) ? (" in " + dto.getLocation()) : "");
        } else {
            genericName = new SimpleDateFormat("yyyy-MM.dd. MMMM").format(dto.getStartDate()) + " "
                + dto.getEventType()
                + (!Strings.isEmpty(dto.getLocation()) ? (" in " + dto.getLocation()) : "");
        }
        return genericName;
    }

    public static URL generateInvitationLink(final String baseUrl, final String token) {
        try {
            final URL url = new URL(baseUrl + "/form/participant?token=" + token);
            final URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Malformed URL", e);
            return null;
        }
    }
}
