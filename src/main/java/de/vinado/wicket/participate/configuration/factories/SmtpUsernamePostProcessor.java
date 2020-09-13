package de.vinado.wicket.participate.configuration.factories;

/**
 * @author Vincent Nadoll
 */
class SmtpUsernamePostProcessor extends DockerSecretProcessor {

    @Override
    protected String getName() {
        return "SMTP username";
    }

    @Override
    protected String getEnvironmentVariable() {
        return "SMTP_USER_FILE";
    }

    @Override
    protected String getSpringProperty() {
        return "spring.mail.username";
    }
}
