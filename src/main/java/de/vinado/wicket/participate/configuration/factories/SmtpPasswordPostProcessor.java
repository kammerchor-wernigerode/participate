package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class SmtpPasswordPostProcessor extends DockerSecretProcessor {

    @Override
    protected String getName() {
        return "SMTP password";
    }

    @Override
    protected String getEnvironmentVariable() {
        return "SMTP_PASSWORD_FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.mail.password";
    }

    @Override
    protected String getSource() {
        return getClass().getSimpleName();
    }
}
