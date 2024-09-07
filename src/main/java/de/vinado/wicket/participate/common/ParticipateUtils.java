package de.vinado.wicket.participate.common;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Slf4j
public class ParticipateUtils {

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
