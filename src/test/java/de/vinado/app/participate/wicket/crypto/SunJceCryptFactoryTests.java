package de.vinado.app.participate.wicket.crypto;

import org.apache.wicket.util.crypt.SunJceCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.apache.wicket.util.crypt.SunJceCrypt.randomSalt;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SunJceCryptFactoryTests {

    private CryptoProperties properties;

    private CryptFactory factory;

    @BeforeEach
    void setUp() {
        properties = mock(CryptoProperties.class);

        factory = new SunJceCryptFactory(properties);
    }

    @Test
    void givenNullDependencies_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new SunJceCryptFactory(null));
    }

    @Test
    void creatingCrypt_shouldInheritSunJceCryptType() {
        when(properties.getPbeSalt()).thenReturn(randomSalt());
        when(properties.getPbeIterationCount()).thenReturn(randomIterationCount());
        when(properties.getSessionSecret()).thenReturn(randomSessionSecret());

        assertInstanceOf(SunJceCrypt.class, factory.create());
    }

    private static int randomIterationCount() {
        return ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
    }

    private static String randomSessionSecret() {
        return UUID.randomUUID().toString();
    }
}
