package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class DatabasePasswordPostProcessor extends DockerSecretProcessor {

    @Override
    protected String getName() {
        return "Database password";
    }

    @Override
    protected String getEnvironmentVariable() {
        return "DATABASE_PASSWORD__FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.datasource.password";
    }

    @Override
    protected Object getSource() {
        return this;
    }
}
