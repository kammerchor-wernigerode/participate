package de.vinado.app.participate.wicket.crypto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import static org.springframework.util.Assert.isTrue;

@Getter
@Setter
@ConfigurationProperties("app.crypto")
class CryptoProperties {

    private String sessionSecret;
    private byte[] pbeSalt;
    private int pbeIterationCount = 17;

    public String getSessionSecret() {
        if (!StringUtils.hasText(sessionSecret)) {
            sessionSecret = RandomStringUtils.randomAlphanumeric(16, 25);
        }
        return sessionSecret;
    }

    public byte[] getPbeSalt() {
        if (pbeSalt.length == 0) {
            pbeSalt = SunJceCrypt.randomSalt();
        }
        isTrue(pbeSalt.length == 8, "Salt must be 8 bytes long");
        return pbeSalt;
    }

    public void setPbeSalt(String pbeSalt) {
        this.pbeSalt = pbeSalt.getBytes();
    }
}
