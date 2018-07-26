package de.vinado.wicket.participate.common;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.data.dto.EventDTO;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Normalizer;
import java.text.SimpleDateFormat;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipateUtils.class);

    public static String getGenericIdentifier(final String string) {
        final String normalized = Normalizer.normalize(string, Normalizer.Form.NFD);
        String result = normalized.replaceAll("[^A-Za-z0-9]", "");
        result = result.replace(" ", "_");
        return result.toUpperCase();
    }

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

    public static URL generateInvitationLink(final String token) {
        try {
            final URL url = new URL(ParticipateApplication.get().getBaseUrl() + "participate?token=" + token);
            final URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
                url.getPath(), url.getQuery(), url.getRef());
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.error("Malformed URL", e);
            return null;
        }
    }
}
