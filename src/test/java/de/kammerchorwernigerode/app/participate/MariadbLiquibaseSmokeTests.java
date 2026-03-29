package de.kammerchorwernigerode.app.participate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mariadb")
@DataJpaTest
class MariadbLiquibaseSmokeTests {

    @Test
    void runningSchemaValidation_shouldSucceed() {
    }
}
