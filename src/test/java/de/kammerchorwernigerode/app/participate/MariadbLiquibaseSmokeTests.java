package de.kammerchorwernigerode.app.participate;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventSummarySuggestionQuery;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@ActiveProfiles("mariadb")
@DataJpaTest
class MariadbLiquibaseSmokeTests {

    @Test
    void runningSchemaValidation_shouldSucceed() {
    }


    @TestConfiguration
    static class MariadbLiquibaseSmokeTestsConfiguration {

        @Bean
        public EventSummarySuggestionQuery clientRegistrationRepository() {
            return mock(EventSummarySuggestionQuery.class);
        }
    }
}
