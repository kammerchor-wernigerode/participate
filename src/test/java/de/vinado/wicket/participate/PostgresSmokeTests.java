package de.vinado.wicket.participate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("postgres")
@DataJpaTest
public class PostgresSmokeTests {

    @Test
    void runningSchemaValidation_shouldSucceed() {
    }
}
