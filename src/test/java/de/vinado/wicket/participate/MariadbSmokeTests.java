package de.vinado.wicket.participate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("mariadb")
@DataJpaTest
public class MariadbSmokeTests {

    @Test
    void runningSchemaValidation_shouldSucceed() {
    }
}
