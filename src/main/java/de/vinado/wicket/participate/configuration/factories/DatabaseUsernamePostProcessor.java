package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class DatabaseUsernamePostProcessor extends DockerSecretProcessor {

    @Override
    protected String getEnvironmentVariable() {
        return "DATABASE_USER_FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.datasource.username";
    }
}
