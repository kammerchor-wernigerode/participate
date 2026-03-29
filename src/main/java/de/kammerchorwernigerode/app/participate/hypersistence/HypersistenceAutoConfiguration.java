package de.kammerchorwernigerode.app.participate.hypersistence;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(after = HibernateJpaAutoConfiguration.class)
@Import(HypersistenceConfiguration.class)
public class HypersistenceAutoConfiguration {
}
