package de.kammerchorwernigerode.app.participate.security.support;

import de.kammerchorwernigerode.app.participate.security.core.AccountUrl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;

@Profile("!oauth2")
@Component
class NullAccountUrl implements AccountUrl {

    @Override
    public URI get() {
        return null;
    }
}
