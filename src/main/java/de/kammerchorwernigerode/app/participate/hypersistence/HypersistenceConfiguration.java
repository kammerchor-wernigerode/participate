package de.kammerchorwernigerode.app.participate.hypersistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.util.ObjectMapperSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jpa.autoconfigure.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Import(HypersistenceConfiguration.HibernateObjectMapperEntityManagerFactoryDependsOnPostProcessor.class)
class HypersistenceConfiguration {

    private ObjectMapper objectMapper;

    @Lazy
    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ObjectMapperSupplier hypersistenceObjectMapper() {
        HypersistenceObjectMapper hypersistenceObjectMapper = new HypersistenceObjectMapper();
        hypersistenceObjectMapper.setObjectMapper(objectMapper);
        return hypersistenceObjectMapper;
    }


    static class HibernateObjectMapperEntityManagerFactoryDependsOnPostProcessor
        extends EntityManagerFactoryDependsOnPostProcessor {

        HibernateObjectMapperEntityManagerFactoryDependsOnPostProcessor() {
            super("hypersistenceObjectMapper");
        }
    }
}
