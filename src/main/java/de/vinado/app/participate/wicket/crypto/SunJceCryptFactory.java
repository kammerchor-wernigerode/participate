package de.vinado.app.participate.wicket.crypto;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CryptoProperties.class)
public class SunJceCryptFactory implements CryptFactory {

    @NonNull
    private final CryptoProperties properties;

    @Override
    public SunJceCrypt create() {
        SunJceCrypt crypt = new SunJceCrypt(properties.getPbeSalt(), properties.getPbeIterationCount());
        crypt.setKey(properties.getSessionSecret());
        return crypt;
    }
}
