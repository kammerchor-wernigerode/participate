package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class DatabasePasswordPostProcessor extends DockerSecretProcessor {

    @Override
    protected String getEnvironmentVariable() {
        return "DATABASE_PASSWORD_FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.datasource.password";
    }
}
