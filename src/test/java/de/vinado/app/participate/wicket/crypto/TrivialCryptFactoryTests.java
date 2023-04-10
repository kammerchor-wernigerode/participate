package de.vinado.app.participate.wicket.crypto;

import org.apache.wicket.util.crypt.TrivialCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrivialCryptFactoryTests {

    private CryptFactory factory;

    @BeforeEach
    void setUp() {
        factory = new TrivialCryptFactory();
    }

    @Test
    void creatingCrypt_shouldInheritTrivialCryptType() {
        assertInstanceOf(TrivialCrypt.class, factory.create());
    }
}
