package de.vinado.app.participate.wicket.crypto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {
    SunJceCryptFactory.class,
    TrivialCryptFactory.class,
})
class CryptConfigurationTests {

    static class Common {

        @Autowired
        protected ApplicationContext context;
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @TestPropertySource(locations = "classpath:de/vinado/app/participate/wicket/crypto/empty.properties")
    class WithoutValue extends Common {

        @Test
        void withoutValue_shouldUseSunJceCryptFactory() {
            assertNotNull(context.getBean(SunJceCryptFactory.class));
        }
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @TestPropertySource(locations = "classpath:de/vinado/app/participate/wicket/crypto/sunjce.properties")
    class WithDeploymentValue extends Common {

        @Test
        void withDevelopmentValue_shouldLoadSunJceCryptFactory() {
            assertNotNull(context.getBean(SunJceCryptFactory.class));
        }
    }

    @Nested
    @ExtendWith(SpringExtension.class)
    @TestPropertySource(locations = "classpath:de/vinado/app/participate/wicket/crypto/trivial.properties")
    class WithDevelopmentValue extends Common {

        @Test
        void withDevelopmentValue_shouldLoadTrivialCryptFactory() {
            assertNotNull(context.getBean(TrivialCryptFactory.class));
        }
    }
}
