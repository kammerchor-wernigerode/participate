package de.kammerchorwernigerode.app.participate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import static org.mockito.Mockito.*;

@SpringBootTest
class ParticipateApplicationSmokeTests {

    @Test
    void loadingContext_shouldSucceed() {
    }


    @TestConfiguration
    static class ParticipateApplicationSmokeTestConfiguration {

        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return mock(ClientRegistrationRepository.class);
        }
    }
}
