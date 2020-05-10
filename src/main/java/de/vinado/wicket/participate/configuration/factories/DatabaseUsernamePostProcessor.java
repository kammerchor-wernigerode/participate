package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class DatabaseUsernamePostProcessor extends DockerSecretProcessor {

    @Override
    protected String getName() {
        return "Database username";
    }

    @Override
    protected String getEnvironmentVariable() {
        return "DATABASE_USER__FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.datasource.username";
    }

    @Override
    protected String getSource() {
        return getClass().getSimpleName();
    }
}
