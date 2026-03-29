package de.kammerchorwernigerode.app.participate.hypersistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.util.ObjectMapperSupplier;

public class HypersistenceObjectMapper implements ObjectMapperSupplier {

    private static ObjectMapper objectMapper;

    public void setObjectMapper(ObjectMapper objectMapper) {
        HypersistenceObjectMapper.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper get() {
        return objectMapper;
    }
}
