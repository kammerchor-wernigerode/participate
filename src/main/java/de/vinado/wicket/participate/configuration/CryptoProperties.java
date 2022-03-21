package de.vinado.wicket.participate.configuration;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

import static org.springframework.util.Assert.isTrue;

/**
 * @author Vincent Nadoll
 */
@Getter
@Setter
@ConfigurationProperties("app.crypto")
public class CryptoProperties {

    private String sessionSecret;
    private String pbeSalt;
    private int pbeIterationCount;

    public String getSessionSecret() {
        if (!StringUtils.hasText(sessionSecret)) {
            sessionSecret = RandomStringUtils.randomAlphanumeric(16, 25);
        }
        return sessionSecret;
    }

    public String getPbeSalt() {
        if (!StringUtils.hasText(pbeSalt)) {
            pbeSalt = new String(SunJceCrypt.randomSalt(), StandardCharsets.UTF_8);
        }
        isTrue(pbeSalt.getBytes(StandardCharsets.UTF_8).length == 8, "Salt must be 8 bytes long");
        return pbeSalt;
    }
}
